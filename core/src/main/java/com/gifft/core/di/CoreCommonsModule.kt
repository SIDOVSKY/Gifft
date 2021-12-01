package com.gifft.core.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.gifft.core.underdevelopment.UnderDevelopmentFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CoreCommonsModule {

    @Binds
    internal abstract fun MultibindingFragmentFactory.bindMultibindingFragmentFactory(): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(UnderDevelopmentFragment::class)
    internal abstract fun UnderDevelopmentFragment.bindUnderDevelopmentFragment(): Fragment
}
