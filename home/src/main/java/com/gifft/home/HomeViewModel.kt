package com.gifft.home

import com.gifft.core.debounce
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class HomeViewModel @Inject constructor() {
    private val _openWrappingCommand = PublishSubject.create<Unit>()
    val openWrappingCommand: Observable<Unit> = _openWrappingCommand

    fun onWrapButtonClick() {
        if (debounce) return
        _openWrappingCommand.onNext(Unit)
    }
}
