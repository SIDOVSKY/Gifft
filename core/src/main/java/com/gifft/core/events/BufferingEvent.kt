@file:Suppress("FunctionName") // Uppercase is OK for factory functions

package com.gifft.core.events

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Creates a simple event channel that buffers sent values when no listeners are subscribed.
 * Buffered values are sent to the first listener subscribed.
 */
fun <T> BufferingEvent(bufferLimit: Int = Int.MAX_VALUE): EventChannel<T> =
    BufferingEventImpl(bufferLimit)

/**
 * Creates a simple event channel that counts the number of event calls when no listeners are subscribed.
 * The first listener to subscribe gets notified that many of times.
 */
@JvmName("UnitBufferingEvent")
fun BufferingEvent(bufferLimit: Int = Int.MAX_VALUE): UnitEventChannel =
    UnitBufferingEventImpl(bufferLimit)

private class UnitBufferingEventImpl(bufferLimit: Int) : BufferingEventImpl<Unit>(bufferLimit),
    UnitEventChannel

private open class BufferingEventImpl<T>(private val bufferLimit: Int) : EventChannel<T> {
    private val buffer = ConcurrentLinkedQueue<T>()
    private val subscribers = mutableSetOf<EventSource.Listener<T>>()

    override fun subscribe(subscriber: EventSource.Listener<T>): Unsubscribe {
        subscribers.add(subscriber)

        buffer.pollEach { event ->
            notifySubscribers(event)
        }

        return { unsubscribe(subscriber) }
    }

    override fun unsubscribe(subscriber: EventSource.Listener<T>) {
        subscribers.remove(subscriber)
    }

    override fun send(value: T) {
        if (subscribers.isEmpty()) {
            keepInBuffer(value)
            return
        }

        notifySubscribers(value)
    }

    private fun keepInBuffer(value: T) {
        buffer.add(value)
        if (buffer.size > bufferLimit) {
            buffer.poll()
        }
    }

    private fun notifySubscribers(value: T) = subscribers.forEach { it.onReceive(value) }

    private inline fun <T> Queue<T>.pollEach(action: (T) -> Unit) {
        while (true) {
            action(poll() ?: break)
        }
    }
}
