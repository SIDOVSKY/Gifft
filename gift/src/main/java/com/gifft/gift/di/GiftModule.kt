package com.gifft.gift.di

import android.content.Context
import com.gifft.gift.GiftRepositoryImpl
import com.gifft.gift.MyObjectBox
import com.gifft.gift.TextGiftLinkBuilderImpl
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGiftLinkBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
internal abstract class GiftModule {
    companion object {
        @Provides
        @Singleton
        fun provideBoxStore(context: Context): BoxStore =
            MyObjectBox.builder().androidContext(context).build()
    }

    @Binds
    abstract fun GiftRepositoryImpl.bindGiftRepositoryImpl(): GiftRepository

    @Binds
    abstract fun TextGiftLinkBuilderImpl.bindTextGiftCoderImpl(): TextGiftLinkBuilder
}
