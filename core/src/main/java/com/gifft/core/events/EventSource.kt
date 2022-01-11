package com.gifft.core.events

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

typealias Unsubscribe = () -> Unit

interface EventSource<T> {
    fun subscribe(subscriber: Listener<T>): Unsubscribe
    fun unsubscribe(subscriber: Listener<T>)

    fun interface Listener<T> {
        fun onReceive(value: T)
    }
}

fun <T> EventSource<T>.asFlow() = callbackFlow {
    val unsubscribe = subscribe { sendBlocking(it) }
    awaitClose(unsubscribe)
}
