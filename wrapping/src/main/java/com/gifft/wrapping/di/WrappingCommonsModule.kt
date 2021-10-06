package com.gifft.wrapping.di

import androidx.fragment.app.Fragment
import com.gifft.core.api.di.FragmentKey
import com.gifft.wrapping.WrappingFragment
import com.gifft.wrapping.WrappingFragmentProviderImpl
import com.gifft.wrapping.api.WrappingFragmentProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WrappingCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(WrappingFragment::class)
    internal abstract fun WrappingFragment.bindWrappingFragment(): Fragment

    @Binds
    internal abstract fun WrappingFragmentProviderImpl.bindWrappingFragmentProviderImpl(): WrappingFragmentProvider
}
