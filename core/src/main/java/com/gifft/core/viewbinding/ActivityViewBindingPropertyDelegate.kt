package com.gifft.core.viewbinding

import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
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
        if (contentView == null || contentView.childCount == 0)
            throw IllegalStateException("Could not find a view in $thisRef")

        if (contentView.childCount != 1)
            throw IllegalStateException("Unexpected activity child count")

        val view = contentView.getChildAt(0)
        return bindView(view).also { viewBinding = it }
    }
}

/**
 * Creates a [ViewBinding] associated with the [ComponentActivity].
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
    @IdRes contentViewId: Int = Window.ID_ANDROID_CONTENT
): ReadOnlyProperty<ComponentActivity, T> =
    ActivityViewBindingPropertyDelegate(bindView, contentViewId)
