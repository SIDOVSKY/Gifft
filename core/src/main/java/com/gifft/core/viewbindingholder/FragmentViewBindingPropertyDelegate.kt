package com.gifft.core.viewbindingholder

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.viewbinding.ViewBinding
import com.gifft.core.ensureMainThread
import com.gifft.core.lifecyclehooks.letAfter

private class FragmentViewBindingPropertyDelegate<T : ViewBinding>(
    private val bindView: (View) -> T
) : ReadOnlyProperty<Fragment, T?> {

    private var viewBinding: T? = null

    /**
     * First access to the binding must be done on the main thread!
     */
    @MainThread
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        viewBinding?.let { return it }

        ensureMainThread { "First access to the binding must be done on the main thread!" }

        val view = thisRef.requireView()

        viewBinding = bindView(view)

        //To not leak memory
        thisRef.viewLifecycleOwner.letAfter(Lifecycle.Event.ON_DESTROY) {
            // Fragment.viewLifecycleOwner call LifecycleObserver.onDestroy()
            // before Fragment.onDestroyView().
            // That's why we need to postpone reset of the viewBinding
            Handler(Looper.getMainLooper()).post {
                viewBinding = null
            }
        }

        return viewBinding
    }
}

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * First access to the binding must be done on the main thread!
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
): ReadOnlyProperty<Fragment, T?> =
    FragmentViewBindingPropertyDelegate(bindView)
