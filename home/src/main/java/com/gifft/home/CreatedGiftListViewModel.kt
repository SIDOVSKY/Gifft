package com.gifft.home

import com.gifft.core.api.gift.GiftRepository
import com.gifft.core.api.gift.TextGift
import com.gifft.wrapping.api.WrappingNavParam
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
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

    val openGiftCommand: Observable<WrappingNavParam> =
        openGiftClickRelay
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .map { WrappingNavParam(it.uuid) }
}
