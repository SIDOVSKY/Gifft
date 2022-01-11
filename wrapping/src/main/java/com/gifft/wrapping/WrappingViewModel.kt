package com.gifft.wrapping

import com.gifft.core.events.BufferingEvent
import com.gifft.core.events.asReceiveOnly
import com.gifft.core.debounce
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.wrapping.api.WrappingNavParam
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class WrappingViewModel @AssistedInject constructor(
    @Assisted navParam: WrappingNavParam,
    @Assisted coroutineScope: CoroutineScope,
    private val giftRepository: GiftRepository,
    private val giftLinkBuilder: TextGiftLinkBuilder
) : CoroutineScope by coroutineScope {
    @AssistedFactory
    interface Factory {
        fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope): WrappingViewModel
    }

    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    private val _originalGift = navParam.existingGiftUuid?.let {
        giftRepository.findGift(it)
    }

    private val _state = MutableStateFlow(VisualState.DEFAULT)
    private val _isSent = MutableStateFlow(_originalGift?.type == GiftType.Sent)
    private val _sender = MutableStateFlow(_originalGift?.sender.orEmpty())
    private val _receiver = MutableStateFlow(_originalGift?.receiver.orEmpty())
    private val _giftContent = MutableStateFlow(_originalGift?.text.orEmpty())

    private val _showErrorEvent = BufferingEvent<String>()
    private val _shareGiftLinkEvent = BufferingEvent<String>()

    val state = _state.asStateFlow()
    val editingEnabled = _isSent.map { !it }
    val sendButtonVisible = _isSent.map { !it }
    val sentLabelVisible = _isSent.asStateFlow()
    val sender = _sender.asStateFlow()
    val receiver = _receiver.asStateFlow()
    val giftContent = _giftContent.asStateFlow()
    val sentDate: String = java.text.SimpleDateFormat.getDateInstance().format(_originalGift?.date ?: Date())

    val showErrorCommand = _showErrorEvent.asReceiveOnly()
    val shareGiftLinkCommand = _shareGiftLinkEvent.asReceiveOnly()

    val exitMode: ExitMode
        get() {
            val newGift = _originalGift == null
            val emptyContent = _sender.value.isBlank()
                    && _receiver.value.isBlank()
                    && _giftContent.value.isBlank()

            val originalGiftChanged = _originalGift?.run {
                sender != _sender.value
                        || receiver != _receiver.value
                        || text != _giftContent.value
            } ?: false

            return when {
                _isSent.value -> ExitMode.NoChanges
                newGift && !emptyContent -> ExitMode.New
                !newGift && emptyContent -> ExitMode.Cleaned
                originalGiftChanged -> ExitMode.Edited
                else -> ExitMode.NoChanges
            }
        }

    fun onSenderInput(newSender: String) {
        _sender.value = newSender
    }

    fun onReceiverInput(newReceiver: String) {
        _receiver.value = newReceiver
    }

    fun onGiftContentInput(newGiftContent: String) {
        _giftContent.value = newGiftContent
    }

    fun onSendGiftClick() {
        if (debounce) return
        sendGift()
    }

    fun onSaveGiftClick() {
        if (debounce) return
        saveGift()
    }

    fun onDeleteGiftClick() {
        if (debounce) return
        deleteGift()
    }

    private fun sendGift() {
        val gift = dumpGift().copy(type = GiftType.Sent)

        if (_sender.value.isBlank()
            || _receiver.value.isBlank()
            || _giftContent.value.isBlank()
        ) {
            _showErrorEvent.send("Cannot send unfinished gift. Please fill every field.")
            return
        }

        launch {
            _state.value = VisualState.IN_PROGRESS
            val link = giftLinkBuilder.build(gift)
            _state.value = VisualState.DEFAULT

            if (link == null) {
                _showErrorEvent.send("Failed to prepare a link. Please try again.")
                return@launch
            }

            saveGift(gift)
            _shareGiftLinkEvent.send(link)
            _isSent.value = true
        }

    }

    private fun saveGift(gift: TextGift? = null) {
        giftRepository.saveTextGift(
            gift ?: dumpGift()
        )
    }

    private fun dumpGift() = TextGift(
        _originalGift?.uuid ?: UUID.randomUUID().toString(),
        _sender.value,
        _receiver.value,
        _originalGift?.date ?: Date(),
        _originalGift?.type ?: GiftType.Created,
        _giftContent.value
    )

    private fun deleteGift() {
        _originalGift?.let { giftRepository.deleteGift(it.uuid) }
    }

    enum class ExitMode {
        NoChanges,
        New,
        Edited,
        Cleaned
    }
}
