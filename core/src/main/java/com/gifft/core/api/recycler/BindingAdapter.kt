package com.gifft.core.api.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class BindingAdapter<Item, Binding : ViewBinding>(
    private val inflate: (LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> Binding,
    diffCallback: DiffUtil.ItemCallback<Item>,
    private val onBind: Binding.(item: Item, position: Int, payloads: List<Any>) -> Unit
) : ListAdapter<Item, BindingHolder<Binding>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BindingHolder(inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BindingHolder<Binding>, position: Int) =
        onBindViewHolder(holder, position, emptyList())

    override fun onBindViewHolder(
        holder: BindingHolder<Binding>,
        position: Int,
        payloads: List<Any>
    ) = holder.binding.onBind(getItem(position), position, payloads)
}

