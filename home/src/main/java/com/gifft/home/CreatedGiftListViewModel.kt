package com.gifft.home

import com.gifft.core.api.gift.Gift
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class CreatedGiftListViewModel : GiftListViewModel() {
    private val _createdGifts = BehaviorSubject.create<List<Gift>>()

    override val gifts: Observable<List<Gift>> = _createdGifts

    init {
        stateMutable.onNext(VisualState.IN_PROGRESS)

        // TODO load async from repository, after dagger
        _createdGifts.onNext(emptyList())

        stateMutable.onNext(VisualState.DEFAULT)
    }
}
