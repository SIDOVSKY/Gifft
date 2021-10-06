package com.gifft.core.api.recycler.rx

import androidx.viewbinding.ViewBinding
import com.gifft.core.api.recycler.BindingHolder
import io.reactivex.disposables.CompositeDisposable

class DisposableBindingHolder<Binding : ViewBinding>(
    binding: Binding
) : BindingHolder<Binding>(binding) {

    val disposables = CompositeDisposable()
}
