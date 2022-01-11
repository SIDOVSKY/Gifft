package com.gifft.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import com.gifft.core.events.EventSource
import com.gifft.core.lifecyclehooks.letAfter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KFunction0

// TODO remove after multiple context receivers
// https://github.com/Kotlin/KEEP/blob/context-receivers/proposals/context-receivers.md
/**
 * Creates subscriptions attached to the [LifecycleOwner.getLifecycle].
 *
 * Can be applied to any [LifecycleOwner]. Supports the Fragments' view-setup callbacks.
 *
 * Subscriptions must be created during the lifecycle callbacks before [State.RESUMED]
 * because they will be disposed in the mirroring ones:
 * ```
 *   Subscribe   |    Dispose
 * --------------|---------------
 * onCreate      | onDestroy
 * onCreateView  | onDestroyView
 * onViewCreated | onDestroyView
 * onStart       | onStop
 * onResume      | onPause
 * ```
 *
 * Subscription callbacks are called on the main thread.
 *
 * @throws IllegalStateException Used on the non-LifecycleOwner type.
 * @throws IllegalStateException Subscribing after onResume.
 */
interface LifecycleAwareSubscriber : FlowLifecycleAwareSubscriber, EventSourceLifecycleAwareSubscriber

interface FlowLifecycleAwareSubscriber : LifecycleAwareSubscriberBase {
    infix fun <T> Flow<T>.observe(onNext: (T) -> Unit) =
        collectOnMainThread(source = this, onNext)

    infix fun <T> Flow<T>.observe(property: KMutableProperty0<T>) =
        collectOnMainThread(source = this, property::set)

    infix fun <T> Flow<T>.observe(onNext: KFunction0<Unit>) =
        collectOnMainThread(source = this) { onNext() }

    private fun <T> collectOnMainThread(source: Flow<T>, onNext: (T) -> Unit) {
        val job = CoroutineScope(Dispatchers.Main.immediate).launch {
            source.collect { newValue ->
                onNext(newValue)
            }
        }
        scheduleDisposeWithLifecycle(job::cancel)
    }
}

interface EventSourceLifecycleAwareSubscriber : LifecycleAwareSubscriberBase {
    infix fun <T> EventSource<T>.observe(onNext: (T) -> Unit) =
        observeOnMainThread(source = this, onNext)

    infix fun <T> EventSource<T>.observe(property: KMutableProperty0<T>) =
        observeOnMainThread(source = this, property::set)

    infix fun <T> EventSource<T>.observe(onNext: KFunction0<Unit>) =
        observeOnMainThread(source = this) { onNext() }

    private fun <T> observeOnMainThread(source: EventSource<T>, onNext: (T) -> Unit) {
        val unsubscribe = source.subscribe { newValue ->
            invokeOnMainThread { onNext(newValue) }
        }
        scheduleDisposeWithLifecycle(unsubscribe)
    }
}

interface LifecycleAwareSubscriberBase

private fun LifecycleAwareSubscriberBase.scheduleDisposeWithLifecycle(dispose: () -> Unit) {
    if (this !is LifecycleOwner)
        throw IllegalStateException("Cannot be used on the non-LifecycleOwner type ${this::class.simpleName}.")

    val lifecycleOwner = when {
        this is Fragment && isOnViewSetup -> viewLifecycleOwner
        else -> this
    }
    val disposeEvent = when (lifecycleOwner.lifecycle.currentState) {
        State.RESUMED -> throw IllegalStateException("Not intended for subscribing after onResume.")
        State.STARTED -> Event.ON_PAUSE
        State.CREATED -> Event.ON_STOP
        else -> Event.ON_DESTROY
    }
    lifecycleOwner.letAfter(disposeEvent) {
        dispose()
    }
}

private val Fragment.isOnViewSetup
    get() = view != null && viewLifecycleOwner.lifecycle.currentState == State.INITIALIZED
