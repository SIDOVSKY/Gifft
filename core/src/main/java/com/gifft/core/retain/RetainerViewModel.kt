package com.gifft.core.retain

import androidx.lifecycle.ViewModel

typealias Object = Any
typealias ObjectClearDelegate = (() -> Unit?)

class RetainerViewModel : ViewModel() {
    val retainedObjects = HashMap<String, Pair<Object, ObjectClearDelegate>>()

    override fun onCleared() {
        super.onCleared()
        retainedObjects.values.forEach { it.second.invoke() }
        retainedObjects.clear()
    }
}
