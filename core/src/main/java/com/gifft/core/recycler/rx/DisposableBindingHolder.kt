package com.gifft.core.recycler.rx

import androidx.viewbinding.ViewBinding
import com.gifft.core.recycler.BindingHolder
import io.reactivex.disposables.CompositeDisposable

class DisposableBindingHolder<Binding : ViewBinding>(
    binding: Binding
) : BindingHolder<Binding>(binding) {

    val disposables = CompositeDisposable()
}
