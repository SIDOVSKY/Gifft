package com.gifft.core.viewbindingholder

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.viewbinding.ViewBinding
import com.gifft.core.invokeOnMainThread
import com.gifft.core.lifecyclehooks.letAfter
import com.gifft.core.mainThreadHandler

private class FragmentViewBindingPropertyDelegate<T : ViewBinding>(
    private val bindView: (View) -> T
) : ReadOnlyProperty<Fragment, T?> {

    private var viewBinding: T? = null

    /**
     * First access to the binding must be done on the main thread!
     */
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        viewBinding?.let { return it }

        val view = thisRef.view ?: return null

        viewBinding = bindView(view)
        schedulePropertyClearingAfterViewDestroyed(propertyOwner = thisRef)

        return viewBinding
    }

    private fun schedulePropertyClearingAfterViewDestroyed(propertyOwner: Fragment) {
        invokeOnMainThread {
            if (propertyOwner.view == null) {
                viewBinding = null
                return@invokeOnMainThread
            }

            propertyOwner.viewLifecycleOwner.letAfter(Lifecycle.Event.ON_DESTROY) {
                // Fragment.viewLifecycleOwner calls LifecycleObserver.onDestroy()
                // before Fragment.onDestroyView().
                // That's why we need to postpone reset of the viewBinding.
                mainThreadHandler.post {
                    viewBinding = null
                }
            }
        }
    }
}

/**
 * Creates a [ViewBinding] associated with the [Fragment].
 *
 * Usage:
 * ```
 * class SomeFragment : Fragment(R.layout.some_fragment) {
 *     private val viewBinding by viewBind(SomeFragmentBinding::bind)
 *
 *     ...
 * }
 * ```
 */
@Suppress("unused")
fun <T : ViewBinding> Fragment.viewBind(
    bindView: (View) -> T
): ReadOnlyProperty<Fragment, T?> = FragmentViewBindingPropertyDelegate(bindView)
