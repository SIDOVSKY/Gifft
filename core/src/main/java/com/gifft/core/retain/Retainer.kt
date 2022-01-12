package com.gifft.core.retain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

typealias onClearDelegate = () -> Unit?

internal class Retainer : ViewModel(), RetainContext {
    private val onClearDelegates = mutableSetOf<onClearDelegate>()

    val retainedValues = HashMap<String, Any>()

    override val retainScope = viewModelScope

    override fun doOnClear(delegate: () -> Unit?) {
        onClearDelegates.add(delegate)
    }

    override fun onCleared() {
        super.onCleared()
        onClearDelegates.forEach { it.invoke() }
        onClearDelegates.clear()
        retainedValues.clear()
    }
}
