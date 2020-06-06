package com.gifft.core.api.di

import androidx.fragment.app.FragmentFactory
import com.gifft.core.api.gift.GiftRepository

interface CoreApiProvider {
    fun provideGiftRepository(): GiftRepository

    fun provideFragmentFactory(): FragmentFactory
}
