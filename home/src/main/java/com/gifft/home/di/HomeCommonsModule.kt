package com.gifft.home.di

import androidx.fragment.app.Fragment
import com.gifft.core.di.FragmentKey
import com.gifft.home.CreatedGiftListFragment
import com.gifft.home.HomeFragment
import com.gifft.home.ReceivedGiftListFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(HomeFragment::class)
    internal abstract fun HomeFragment.bindHomeFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CreatedGiftListFragment::class)
    internal abstract fun CreatedGiftListFragment.bindCreatedGiftListFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ReceivedGiftListFragment::class)
    internal abstract fun ReceivedGiftListFragment.bindReceivedGiftListFragment(): Fragment
}
