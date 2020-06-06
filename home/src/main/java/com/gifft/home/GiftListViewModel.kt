package com.gifft.home

import android.annotation.SuppressLint
import com.gifft.core.api.gift.GiftRepository
import com.gifft.core.api.gift.TextGift
import com.gifft.wrapping.api.WrappingNavParam
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
abstract class GiftListViewModel constructor(
    private val giftRepository: GiftRepository
) {
    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    protected val openGiftClickRelay = PublishRelay.create<TextGift>()
    private val _deleteClickRelay = PublishRelay.create<TextGift>()

    protected val stateMutable = BehaviorSubject.create<VisualState>()

    protected val disposable = CompositeDisposable()

    val state: Observable<VisualState> = stateMutable
    abstract val gifts: Observable<List<TextGift>>

    init {
        _deleteClickRelay
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribe { deleteGift(it) }
    }

    val deleteButtonClick: Consumer<TextGift> = _deleteClickRelay
    val openGiftClick: Consumer<TextGift> = openGiftClickRelay

    fun dispose() = disposable.dispose()

    private fun deleteGift(textGift: TextGift) {
        giftRepository.deleteGift(textGift.uuid)
    }
}
