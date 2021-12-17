package com.gifft.core.retain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.lifecycle.viewModelScope
import com.gifft.core.lifecyclehooks.letAfter
import kotlinx.coroutines.CoroutineScope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * usage:
 * ```
 * private val viewModel by retain(
 *     producer = { NonAndroidViewModel() },
 *     onClean = { dispose() })
 * ```
 */
fun <Owner, T : Any> Owner.retain(
    producer: (retainScope: CoroutineScope) -> T,
    onClean: (T.() -> Unit)
): ReadOnlyProperty<Owner, T>
        where Owner : ViewModelStoreOwner,
              Owner : LifecycleOwner =
    RetainProperty(this, producer, onClean)

/**
 * usage:
 * ```
 * private val viewModel by retain { NonAndroidViewModel() }
 * ```
 */
fun <Owner, T : Any> Owner.retain(
    producer: (retainScope: CoroutineScope) -> T
): ReadOnlyProperty<Owner, T>
        where Owner : ViewModelStoreOwner,
              Owner : LifecycleOwner =
    RetainProperty(this, producer, null)

private class RetainProperty<Owner, T : Any>(
    owner: Owner,
    private val producer: (retainScope: CoroutineScope) -> T,
    private val clearer: (T.() -> Unit)? = null
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
        val value = cached

        return if (value == null) {
            if (!::retainer.isInitialized) {
                retainer = ViewModelProvider(thisRef).get()
            }

            @Suppress("UNCHECKED_CAST")
            val retainedValue = retainer.retainedObjects[property.name]?.first as? T
                ?: producer.invoke(retainer.viewModelScope).also { retainValue ->
                    retainer.retainedObjects[property.name] = Pair(retainValue, { clearer?.invoke(retainValue) })
                }

            cached = retainedValue

            retainedValue
        } else {
            value
        }
    }
}
