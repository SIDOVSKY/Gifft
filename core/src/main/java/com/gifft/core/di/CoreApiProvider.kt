package com.gifft.core.di

import androidx.fragment.app.FragmentFactory

interface CoreApiProvider {
    fun provideFragmentFactory(): FragmentFactory
}
