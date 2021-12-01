package com.gifft.home

import com.gifft.core.debounce
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class GiftListViewModel constructor(
    private val giftRepository: GiftRepository
) {
    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    protected val openGiftEvent = PublishSubject.create<TextGift>()

    protected val stateMutable = BehaviorSubject.create<VisualState>()

    protected val disposable = CompositeDisposable()

    val state: Observable<VisualState> = stateMutable
    abstract val gifts: Observable<List<TextGift>>

    fun onOpenGiftClick(giftToOpen: TextGift) {
        if (debounce) return
        openGiftEvent.onNext(giftToOpen)
    }

    fun onDeleteButtonClick(giftToDelete: TextGift) {
        if (debounce) return
        giftRepository.deleteGift(giftToDelete.uuid)
    }

    fun dispose() = disposable.dispose()
}
