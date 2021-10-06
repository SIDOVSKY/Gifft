package com.gifft.wrapping

import androidx.fragment.app.Fragment
import com.gifft.wrapping.api.WrappingFragmentProvider
import javax.inject.Inject

internal class WrappingFragmentProviderImpl @Inject constructor() : WrappingFragmentProvider {
    override fun provideClass(): Class<out Fragment> = WrappingFragment::class.java
}
