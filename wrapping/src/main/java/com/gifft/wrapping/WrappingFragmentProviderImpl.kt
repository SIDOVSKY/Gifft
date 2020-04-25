package com.gifft.wrapping

import androidx.fragment.app.Fragment
import com.gifft.wrapping.api.WrappingFragmentProvider

class WrappingFragmentProviderImpl : WrappingFragmentProvider {
    override fun provideClass(): Class<out Fragment> = WrappingFragment::class.java
}
