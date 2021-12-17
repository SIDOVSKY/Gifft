package com.gifft.core

import android.os.Handler
import android.os.Looper

val mainThreadHandler = Handler(Looper.getMainLooper())

inline val isMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

inline fun ensureMainThread(lazyMessage: () -> Any = { "Call is out of MainThread" }) =
    check(isMainThread, lazyMessage)

fun invokeOnMainThread(action: () -> Unit) {
    if (isMainThread) {
        action()
    } else {
        mainThreadHandler.post(action)
    }
}