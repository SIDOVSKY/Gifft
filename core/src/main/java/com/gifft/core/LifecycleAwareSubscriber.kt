package com.gifft.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import com.gifft.core.lifecyclehooks.letAfter
import io.reactivex.Observable
import io.reactivex.functions.Consumer
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
interface LifecycleAwareSubscriber : RxJavaLifecycleAwareSubscriber

interface RxJavaLifecycleAwareSubscriber : LifecycleAwareSubscriberBase {
    infix fun <T> Observable<T>.observe(onNext: (T) -> Unit) =
        observeOnMainThread(source = this, onNext)

    infix fun <T> Observable<T>.observe(property: KMutableProperty0<T>) =
        observeOnMainThread(source = this, property::set)

    infix fun <T> Observable<T>.observe(onNext: KFunction0<Unit>) =
        observeOnMainThread(source = this) { onNext() }

    infix fun <T> Observable<T>.observe(consumer: Consumer<T>) =
        observeOnMainThread(source = this, consumer::accept)

    private fun <T> observeOnMainThread(source: Observable<T>, onNext: (T) -> Unit) {
        val disposable = source.subscribe { newValue ->
            invokeOnMainThread { onNext(newValue) }
        }
        scheduleDisposeWithLifecycle(disposable::dispose)
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
