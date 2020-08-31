package com.gifft.unwrapping.di

import androidx.fragment.app.Fragment
import com.gifft.core.api.di.FragmentKey
import com.gifft.unwrapping.UnwrappingFragment
import com.gifft.unwrapping.UnwrappingFragmentProviderImpl
import com.gifft.unwrapping.api.UnwrappingFragmentProvider
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@AssistedModule
@Module(includes = [AssistedInject_UnwrappingCommonsModule::class])
interface UnwrappingCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(UnwrappingFragment::class)
    fun UnwrappingFragment.bindUnwrappingFragment(): Fragment

    @Binds
    fun UnwrappingFragmentProviderImpl.bindWrappingFragmentProviderImpl(): UnwrappingFragmentProvider
}
