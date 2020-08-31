package com.gifft.unwrapping

import androidx.fragment.app.Fragment
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import javax.inject.Inject

class UnwrappingFragmentProviderImpl @Inject constructor() : UnwrappingFragmentProvider {
    override fun provideClass(): Class<out Fragment> = UnwrappingFragment::class.java
}
