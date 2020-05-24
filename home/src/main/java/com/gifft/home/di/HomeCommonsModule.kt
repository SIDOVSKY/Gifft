package com.gifft.home.di

import androidx.fragment.app.Fragment
import com.gifft.core.api.di.FragmentKey
import com.gifft.home.CreatedGiftListFragment
import com.gifft.home.HomeFragment
import com.gifft.home.ReceivedGiftListFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface HomeCommonsModule {
    @Binds
    @IntoMap
    @FragmentKey(HomeFragment::class)
    fun HomeFragment.bindHomeFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(CreatedGiftListFragment::class)
    fun CreatedGiftListFragment.bindCreatedGiftListFragment(): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ReceivedGiftListFragment::class)
    fun ReceivedGiftListFragment.bindReceivedGiftListFragment(): Fragment
}
