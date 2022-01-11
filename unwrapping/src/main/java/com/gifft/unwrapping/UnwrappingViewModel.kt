package com.gifft.unwrapping

import android.net.Uri
import com.gifft.core.debounce
import com.gifft.core.events.asReceiveOnly
import com.gifft.core.events.BufferingEvent
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.unwrapping.api.UnwrappingNavParam
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UnwrappingViewModel @AssistedInject constructor(
    @Assisted val navParam: UnwrappingNavParam,
    private val giftRepository: GiftRepository,
    private val giftLinkBuilder: TextGiftLinkBuilder
) {
    @AssistedFactory
    interface Factory {
        fun create(navParam: UnwrappingNavParam): UnwrappingViewModel
    }

    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    private val _state = MutableStateFlow(VisualState.DEFAULT)
    private val _sender = MutableStateFlow("")
    private val _receiver = MutableStateFlow("")
    private val _giftContent = MutableStateFlow("")

    private val _fatalError = BufferingEvent<String>()
    private val _goHomeCommand = BufferingEvent(bufferLimit = 1)

    val state = _state.asStateFlow()
    val sender = _sender.asStateFlow()
    val receiver = _receiver.asStateFlow()
    val giftContent = _giftContent.asStateFlow()

    val goHomeCommand = _goHomeCommand.asReceiveOnly()
    val fatalError = _fatalError.asReceiveOnly()

    suspend fun init() {
        val link = Uri.parse(navParam.uriOrUuid)

        val existingGift: TextGift?
        val gift: TextGift?

        if (link.host != null) {
            _state.value = VisualState.IN_PROGRESS

            val linkGift = giftLinkBuilder.parse(link)
            existingGift = linkGift?.let { giftRepository.findGift(it.uuid) }
            gift = existingGift ?: linkGift?.copy(type = GiftType.Received)
        } else {
            existingGift = giftRepository.findGift(navParam.uriOrUuid)
            gift = existingGift
        }

        if (gift == null) {
            _fatalError.send("Could not open the gift. Please try again.")
            return
        }

        if (existingGift == null) {
            giftRepository.saveTextGift(gift)
        }

        _sender.value = gift.sender
        _receiver.value = gift.receiver
        _giftContent.value = gift.text

        _state.value = VisualState.DEFAULT
    }

    fun onGoToAllGiftsClick() {
        if (debounce) return
        _goHomeCommand.send()
    }
}
