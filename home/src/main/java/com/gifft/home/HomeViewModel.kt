package com.gifft.home

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeViewModel @Inject constructor() {
    private val _wrapClickOpenRelay = PublishRelay.create<Unit>()

    val wrapButtonClick: Consumer<Unit> = _wrapClickOpenRelay

    val openWrappingCommand: Observable<Unit> = _wrapClickOpenRelay
        .throttleFirst(300, TimeUnit.MILLISECONDS)
}
