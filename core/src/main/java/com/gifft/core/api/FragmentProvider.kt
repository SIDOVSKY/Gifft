package com.gifft.core.api

import androidx.fragment.app.Fragment

fun interface FragmentProvider {
    fun provideClass(): Class<out Fragment>
}