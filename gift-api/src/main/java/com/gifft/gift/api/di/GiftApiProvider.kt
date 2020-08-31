package com.gifft.gift.api.di

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGiftLinkBuilder

interface GiftApiProvider {
    fun provideGiftRepository(): GiftRepository

    fun provideTextGiftLinkBuilder(): TextGiftLinkBuilder
}
