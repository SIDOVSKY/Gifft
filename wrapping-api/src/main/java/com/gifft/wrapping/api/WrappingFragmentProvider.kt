package com.gifft.wrapping.api

import androidx.fragment.app.Fragment

interface WrappingFragmentProvider {
    fun provideClass() : Class<out Fragment>
}
