package com.gifft.home

import com.gifft.core.api.gift.Gift
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ReceivedGiftListViewModel : GiftListViewModel() {
    private val _receivedGifts = BehaviorSubject.create<List<Gift>>()

    override val gifts: Observable<List<Gift>> = _receivedGifts

    init {
        stateMutable.onNext(VisualState.IN_PROGRESS)

        // TODO load async from repository, after dagger
        _receivedGifts.onNext(emptyList())

        stateMutable.onNext(VisualState.DEFAULT)
    }
}
