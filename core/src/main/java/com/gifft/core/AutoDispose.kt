package com.gifft.core

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Call this method before OnPause
 */
fun Disposable.autoDispose(
    lifecycleOwner: LifecycleOwner,
    disposeEvent: Event = eventMirroringState(lifecycleOwner.lifecycle.currentState),
): Disposable = apply {
    autoDispose(lifecycleOwner, disposeEvent, this)
}

fun Array<Disposable>.autoDispose(
    lifecycleOwner: LifecycleOwner,
    disposeEvent: Event = eventMirroringState(lifecycleOwner.lifecycle.currentState),
) = autoDispose(lifecycleOwner, disposeEvent, *this)

/**
 * Call this method before OnPause
 */
fun autoDispose(
    lifecycleOwner: LifecycleOwner,
    disposeEvent: Event = eventMirroringState(lifecycleOwner.lifecycle.currentState),
    vararg disposables: Disposable
) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Event) {
            if (event == disposeEvent) {
                disposables.forEach { it.dispose() }
                source.lifecycle.removeObserver(this)
            }
        }
    })
}

private fun eventMirroringState(state: State) = when (state) {
    State.STARTED -> Event.ON_PAUSE
    State.CREATED -> Event.ON_STOP
    else -> Event.ON_DESTROY
}
