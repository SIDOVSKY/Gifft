package com.gifft.core.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.gifft.core.api.di.FragmentKey
import com.gifft.core.api.underdevelopment.UnderDevelopmentFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CoreCommonsModule {

    @Binds
    fun MultibindingFragmentFactory.bindMultibindingFragmentFactory(): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(UnderDevelopmentFragment::class)
    fun UnderDevelopmentFragment.bindUnderDevelopmentFragment(): Fragment
}
