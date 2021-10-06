package com.gifft.core.api

import android.os.Looper

inline val isMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

fun ensureMainThread(lazyMessage: () -> Any = { "Call is out of MainThread" }) =
    check(isMainThread, lazyMessage)
