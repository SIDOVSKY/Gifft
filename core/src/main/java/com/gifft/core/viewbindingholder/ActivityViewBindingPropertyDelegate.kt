package com.gifft.core.viewbindingholder

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.gifft.core.lifecyclehooks.letAfter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class ActivityViewBindingPropertyDelegate<T : ViewBinding>(
    private val bindView: (View) -> T,
    @IdRes private val contentViewId: Int
) : ReadOnlyProperty<ComponentActivity, T> {

    private var viewBinding: T? = null

    override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
        viewBinding?.let { return it }

        val contentView = thisRef.findViewById<View>(contentViewId) as? ViewGroup
            ?: throw IllegalStateException("Could not find a view in $thisRef")

        if (contentView.childCount == 0)
            throw IllegalStateException("Could not find a view in $thisRef")

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
    bindView: (View) -> T,
    @IdRes contentViewId: Int = android.R.id.content
): ReadOnlyProperty<ComponentActivity, T> =
    ActivityViewBindingPropertyDelegate(bindView, contentViewId)
