package com.gifft.core.api.viewbindingholder

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.gifft.core.api.lifecyclehooks.letAfter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class ActivityViewBindingPropertyDelegate<T : ViewBinding>(
    private val bindView: (View) -> T
) : ReadOnlyProperty<ComponentActivity, T> {

    private var viewBinding: T? = null

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
        viewBinding?.let { return it }

        if (thisRef.lifecycle.currentState == Lifecycle.State.INITIALIZED)
            throw IllegalStateException("Cannot access view binding before activity created")

        if (thisRef.lifecycle.currentState.isAtLeast(Lifecycle.State.DESTROYED))
            throw IllegalStateException("Cannot access view binding after activity destroy")

        val contentView = thisRef.findViewById<View>(android.R.id.content) as ViewGroup

        if (contentView.childCount != 1)
            throw IllegalStateException("Unexpected activity child count")

        val view = contentView.getChildAt(0)

        val binding = bindView(view).also { viewBinding = it }

        thisRef.letAfter(Lifecycle.Event.ON_DESTROY) {
            viewBinding = null
        }

        return binding
    }
}

/**
 * Create new [ViewBinding] associated with the [ComponentActivity] and allow customize how
 * a [View] will be bounded to the view binding.
 *
 * Usage:
 * ```
 * class SomeActivity : AppCompatActivity(R.layout.some_activity) {
 *     private val viewBinding by viewBind(SomeActivityBinding::bind)
 *
 *     ...
 * }
 * ```
 */
@Suppress("unused")
fun <T : ViewBinding> ComponentActivity.viewBind(
    bindView: (View) -> T
): ReadOnlyProperty<ComponentActivity, T> =
    ActivityViewBindingPropertyDelegate(bindView)
