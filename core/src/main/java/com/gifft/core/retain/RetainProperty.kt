package com.gifft.core.retain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import com.gifft.core.lifecyclehooks.letAfter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Retain property value between activity configuration changes.
 *
 * **Simple usage:**
 * ```
 * private val viewModel by retain { NonAndroidViewModel() }
 * ```
 *
 * **Advanced usage:**
 * ```
 * private val viewModel by retain {
 *     NonAndroidViewModel(coroutineScope = retainScope).apply {
 *         doOnClear { dispose() }
 *     }
 * }
 * ```
 */
fun <Owner, T : Any> Owner.retain(create: RetainContext.() -> T): ReadOnlyProperty<Owner, T>
        where Owner : ViewModelStoreOwner,
              Owner : LifecycleOwner =
    RetainProperty(this, create)

private class RetainProperty<Owner, T : Any>(
    owner: Owner,
    private val create: RetainContext.() -> T,
) : ReadOnlyProperty<ViewModelStoreOwner, T>
        where Owner : ViewModelStoreOwner,
              Owner : LifecycleOwner {

    private lateinit var retainer: RetainerViewModel

    private var cached: T? = null

    init {
        // Hook init after OnCreate instead of getValue because
        // android ViewModel access requires MainThread and an attached owner.
        // Let getValue be thread safe in most cases.
        owner.letAfter(Lifecycle.Event.ON_CREATE) {
            if (!::retainer.isInitialized) {
                retainer = ViewModelProvider(it).get()
            }
        }
    }

    override fun getValue(thisRef: ViewModelStoreOwner, property: KProperty<*>): T {
        cached?.let { return it }

        if (!::retainer.isInitialized) {
            retainer = ViewModelProvider(thisRef).get()
        }

        @Suppress("UNCHECKED_CAST")
        val retainValue = retainer.retainedObjects.getOrPut(key = property.name) { create(retainer) } as T
        return retainValue.also { cached = it }
    }
}
