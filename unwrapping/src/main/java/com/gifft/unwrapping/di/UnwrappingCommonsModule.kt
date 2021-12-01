package com.gifft.unwrapping.di

import androidx.fragment.app.Fragment
import com.gifft.core.di.FragmentKey
import com.gifft.unwrapping.UnwrappingFragment
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class UnwrappingCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(UnwrappingFragment::class)
    internal abstract fun UnwrappingFragment.bindUnwrappingFragment(): Fragment

    companion object {
        @Provides
        internal fun provideUnwrappingFragmentProvider() =
            UnwrappingFragmentProvider { UnwrappingFragment::class.java }
    }
}
