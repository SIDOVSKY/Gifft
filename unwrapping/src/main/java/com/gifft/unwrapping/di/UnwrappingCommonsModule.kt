package com.gifft.unwrapping.di

import androidx.fragment.app.Fragment
import com.gifft.core.api.di.FragmentKey
import com.gifft.unwrapping.UnwrappingFragment
import com.gifft.unwrapping.UnwrappingFragmentProviderImpl
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UnwrappingCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(UnwrappingFragment::class)
    internal abstract fun UnwrappingFragment.bindUnwrappingFragment(): Fragment

    @Binds
    internal abstract fun UnwrappingFragmentProviderImpl.bindWrappingFragmentProviderImpl(): UnwrappingFragmentProvider
}
