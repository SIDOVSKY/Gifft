package com.gifft.core.api.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BindingHolder<Binding : ViewBinding>(val binding: Binding) :
    RecyclerView.ViewHolder(binding.root)
