package com.gifft.home

import com.gifft.core.api.gift.GiftRepository
import com.gifft.core.api.gift.TextGift
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class ReceivedGiftListViewModel @Inject constructor(
    giftRepository: GiftRepository
) : GiftListViewModel(giftRepository) {
    private val _receivedGifts = BehaviorSubject.create<List<TextGift>>()

    override val gifts: Observable<List<TextGift>> = _receivedGifts

    init {
        stateMutable.onNext(VisualState.IN_PROGRESS)

        _receivedGifts
            .take(1)
            .subscribe { stateMutable.onNext(VisualState.DEFAULT) }
            .addTo(disposable)

        giftRepository.allReceivedGifts()
            .doOnSubscribe { disposable.add(it) }
            .subscribe(_receivedGifts)
    }
}
