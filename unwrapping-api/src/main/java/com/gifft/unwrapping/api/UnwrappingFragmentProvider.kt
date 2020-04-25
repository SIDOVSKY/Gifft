package com.gifft.unwrapping.api

import androidx.fragment.app.Fragment

interface UnwrappingFragmentProvider {
    fun provideClass() : Class<out Fragment>
}
