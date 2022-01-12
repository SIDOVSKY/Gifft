package com.gifft.core.retain

import kotlinx.coroutines.CoroutineScope

interface RetainContext {
    val retainScope: CoroutineScope

    fun doOnClear(delegate: () -> Unit?)
}