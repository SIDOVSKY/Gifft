package com.gifft.core.api.di

import androidx.fragment.app.FragmentFactory

interface CoreApiProvider {
    fun provideFragmentFactory(): FragmentFactory
}
