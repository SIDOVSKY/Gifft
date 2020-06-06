package com.gifft.core.api.recycler

import androidx.recyclerview.widget.DiffUtil

class DelegateDiffCallback<T>(
    private val itemsSame: (oldItem: T, newItem: T) -> Boolean,
    private val contentsSame: (oldItem: T, newItem: T) -> Boolean
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) =
        itemsSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        contentsSame(oldItem, newItem)
}
