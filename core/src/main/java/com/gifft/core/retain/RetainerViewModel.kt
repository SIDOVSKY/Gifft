package com.gifft.core.retain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

typealias onClearDelegate = () -> Unit?

internal class RetainerViewModel : ViewModel(), RetainContext {
    private val onClearDelegates = mutableSetOf<onClearDelegate>()

    val retainedObjects = HashMap<String, Any>()

    override val retainScope = viewModelScope

    override fun doOnClear(delegate: () -> Unit?) {
        onClearDelegates.add(delegate)
    }

    override fun onCleared() {
        super.onCleared()
        onClearDelegates.forEach { it.invoke() }
        onClearDelegates.clear()
        retainedObjects.clear()
    }
}
