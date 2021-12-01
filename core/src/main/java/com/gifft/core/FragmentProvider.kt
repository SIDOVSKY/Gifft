package com.gifft.core

import androidx.fragment.app.Fragment

fun interface FragmentProvider {
    fun provideClass(): Class<out Fragment>
}