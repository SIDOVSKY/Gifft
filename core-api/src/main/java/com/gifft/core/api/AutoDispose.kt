package com.gifft.core.api

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Call this method before OnPause
 */
fun Disposable.autoDispose(
    lifecycleOwner: LifecycleOwner, disposeEvent: Lifecycle.Event
): Disposable = apply {
    autoDispose(lifecycleOwner, disposeEvent, this)
}

fun Array<Disposable>.autoDispose(lifecycleOwner: LifecycleOwner, disposeEvent: Lifecycle.Event) =
    autoDispose(lifecycleOwner, disposeEvent, *this)

/**
 * Call this method before OnPause
 */
fun autoDispose(
    lifecycleOwner: LifecycleOwner,
    disposeEvent: Lifecycle.Event,
    vararg disposables: Disposable
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == disposeEvent) {
                disposables.forEach { it.dispose() }
                source.lifecycle.removeObserver(this)
            }
        }
    })
}
