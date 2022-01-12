package com.gifft.core.lifecyclehooks

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@MainThread
fun <T: LifecycleOwner, R> T.letAfter(event: Lifecycle.Event, block: (T) -> R) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, happenedEvent: Lifecycle.Event) {
            if (happenedEvent == event) {
                block.invoke(this@letAfter)
                source.lifecycle.removeObserver(this)
            } else if (happenedEvent == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
            }
        }
    })
}

@MainThread
fun <T: LifecycleOwner, R> T.letAfterEach(event: Lifecycle.Event, block: (T) -> R) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, happenedEvent: Lifecycle.Event) {
            if (happenedEvent == event) {
                block.invoke(this@letAfterEach)
            }
            if (happenedEvent == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
            }
        }
    })
}
