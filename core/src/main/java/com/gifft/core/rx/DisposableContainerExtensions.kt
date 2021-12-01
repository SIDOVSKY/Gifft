package com.gifft.core.rx

import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

fun DisposableContainer.add(vararg disposable: Disposable) {
    disposable.forEach { add(it) }
}
