package com.gifft.core.api.recycler.rx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.reactivex.internal.disposables.DisposableContainer

class RxBindingAdapter<Item, Binding : ViewBinding>(
    private val inflate: (LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> Binding,
    diffCallback: DiffUtil.ItemCallback<Item>,
    private val onBind: Binding.(
        item: Item,
        disposable: DisposableContainer,
        position: Int,
        payloads: List<Any>
    ) -> Unit
) : ListAdapter<Item, DisposableBindingHolder<Binding>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DisposableBindingHolder(
            inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: DisposableBindingHolder<Binding>, position: Int) =
        onBindViewHolder(holder, position, emptyList())

    override fun onBindViewHolder(
        holder: DisposableBindingHolder<Binding>,
        position: Int,
        payloads: List<Any>
    ) {
        holder.disposables.clear()
        holder.binding.onBind(getItem(position), holder.disposables, position, payloads)
    }

    override fun onViewRecycled(holder: DisposableBindingHolder<Binding>) {
        super.onViewRecycled(holder)

        holder.disposables.clear()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        recyclerView.children.forEach {
            val holder = recyclerView.findContainingViewHolder(it) as DisposableBindingHolder<*>
            holder.disposables.clear()
        }
    }
}
