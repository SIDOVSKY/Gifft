package com.gifft.home

import com.gifft.core.api.gift.Gift
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class GiftListViewModel {
    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    protected val stateMutable = BehaviorSubject.create<VisualState>()

    val state: Observable<VisualState> = stateMutable
    abstract val gifts: Observable<List<Gift>>
}
