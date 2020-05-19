package com.gifft.wrapping.api.di

import com.gifft.wrapping.api.WrappingFragmentProvider

interface WrappingApiProvider {
    fun provideWrappingFragmentProvider(): WrappingFragmentProvider
}
