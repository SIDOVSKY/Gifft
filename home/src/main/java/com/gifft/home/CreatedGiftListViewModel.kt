package com.gifft.home

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.wrapping.api.WrappingNavParam
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class CreatedGiftListViewModel @Inject constructor(
    giftRepository: GiftRepository
) : GiftListViewModel(giftRepository) {
    private val _createdGifts = BehaviorSubject.create<List<TextGift>>()

    override val gifts: Observable<List<TextGift>> = _createdGifts

    init {
        stateMutable.onNext(VisualState.IN_PROGRESS)

        _createdGifts
            .take(1)
            .subscribe { stateMutable.onNext(VisualState.DEFAULT) }
            .addTo(disposable)

        giftRepository.allCreatedGifts()
            .doOnSubscribe { disposable.add(it) }
            .subscribe(_createdGifts)
    }

    val openGiftCommand: Observable<WrappingNavParam> = openGiftEvent
        .map { WrappingNavParam(it.uuid) }
}
