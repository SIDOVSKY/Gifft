package com.gifft.core.api

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.LifecycleEventObserver

/**
 * Not tied to android platform, can become expect/actual for multiplatform
 */
interface Pausable : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onResume()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onPause()
}

fun <T : Pausable> T.registerPauseWithLifecycle(lifecycleOwner: LifecycleOwner): T = apply {
    lifecycleOwner.lifecycle.addObserver(this)

    val unregisterer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this@apply)
                source.lifecycle.removeObserver(this)
            }
        }
    }

    lifecycleOwner.lifecycle.addObserver(unregisterer)
}
