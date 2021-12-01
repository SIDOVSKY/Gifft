package com.gifft.unwrapping

import android.net.Uri
import com.gifft.core.debounce
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.unwrapping.api.UnwrappingNavParam
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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

    private val stateMutable = BehaviorSubject.create<VisualState>()
    private val _senderSubject = BehaviorSubject.create<String>()
    private val _receiverSubject = BehaviorSubject.create<String>()
    private val _giftContentSubject = BehaviorSubject.create<String>()

    private val _fatalError = PublishSubject.create<String>()
    private val _goHomeCommand = PublishSubject.create<Unit>()

    val state: Observable<VisualState> = stateMutable
    val sender: Observable<String> = _senderSubject
    val receiver: Observable<String> = _receiverSubject
    val giftContent: Observable<String> = _giftContentSubject

    val goHomeCommand: Observable<Unit> = _goHomeCommand
    val fatalError: Observable<String> = _fatalError

    suspend fun init() {
        val link = Uri.parse(navParam.uriOrUuid)

        val existingGift: TextGift?
        val gift: TextGift?

        if (link.host != null) {
            stateMutable.onNext(VisualState.IN_PROGRESS)

            val linkGift = giftLinkBuilder.parse(link)
            existingGift = linkGift?.let { giftRepository.findGift(it.uuid) }
            gift = existingGift ?: linkGift?.copy(type = GiftType.Received)
        } else {
            existingGift = giftRepository.findGift(navParam.uriOrUuid)
            gift = existingGift
        }

        if (gift == null) {
            _fatalError.onNext("Could not open the gift. Please try again.")
            return
        }

        if (existingGift == null) {
            giftRepository.saveTextGift(gift)
        }

        _senderSubject.onNext(gift.sender)
        _receiverSubject.onNext(gift.receiver)
        _giftContentSubject.onNext(gift.text)

        stateMutable.onNext(VisualState.DEFAULT)
    }

    fun onGoToAllGiftsClick() {
        if (debounce) return
        _goHomeCommand.onNext(Unit)
    }
}
