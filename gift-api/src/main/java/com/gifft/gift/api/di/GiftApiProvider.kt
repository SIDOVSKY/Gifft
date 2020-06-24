package com.gifft.gift.api.di

import com.gifft.gift.api.GiftRepository

interface GiftApiProvider {
    fun provideGiftRepository(): GiftRepository
}
