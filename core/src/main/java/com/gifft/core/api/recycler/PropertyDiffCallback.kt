package com.gifft.core.api.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import kotlin.reflect.KProperty1

/**
 * Usage:
 * ```
 * PropertyDiffCallback(
 *     identityProperty = YourClass::id,
 *     contentProperties = sequenceOf(
 *         YourClass::date,
 *         YourClass::sender,
 *         YourClass::text
 *     )
 * )
 * ```
 */
class PropertyDiffCallback<T>(
    private val identityProperties: Sequence<KProperty1<T, *>>,
    private val contentProperties: Sequence<KProperty1<T, *>>
) : DiffUtil.ItemCallback<T>() {

    constructor(
        identityProperty: KProperty1<T, *>,
        contentProperties: Sequence<KProperty1<T, *>>
    ) : this(sequenceOf(identityProperty), contentProperties)

    override fun areItemsTheSame(oldItem: T, newItem: T) =
        identityProperties.all { it.get(oldItem) == it.get(newItem) }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) =
        contentProperties.all { it.get(oldItem) == it.get(newItem) }
}
