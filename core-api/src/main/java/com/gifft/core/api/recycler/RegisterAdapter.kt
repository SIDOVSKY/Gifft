package com.gifft.core.api.recycler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.gifft.core.api.lifecyclehooks.letAfter

// TODO write lint rule
/**
 * Used instead of [RecyclerView.setAdapter]
 * to avoid leaks when fragment has a strong reference to the settable adapter
 *
 * see
 * https://medium.com/@yfujiki/tracing-simple-memory-leak-around-recyclerview-using-leakcanary-927460532d53
 * https://charlesmuchene.hashnode.dev/a-subtle-memory-leak-fragment-recyclerview-and-its-adapter-ck805s7jd03frzns17uapi3vh
 */
fun RecyclerView.setAdapter(
    adapter: RecyclerView.Adapter<*>,
    viewLifecycleOwner: LifecycleOwner
) {
    this.adapter = adapter

    viewLifecycleOwner.letAfter(Lifecycle.Event.ON_DESTROY) {
        this.adapter = null
    }
}
