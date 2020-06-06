package com.gifft.core.api.rx

import io.reactivex.Observable

fun <T> Observable<T>.doOnNextOne(onNextOne: (T) -> Unit): Observable<T> {
    var firstPassed = false

    return doOnNext {
        if (!firstPassed) {
            firstPassed = true
            onNextOne(it)
        }
    }
}
