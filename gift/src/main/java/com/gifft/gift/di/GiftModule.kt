package com.gifft.gift.di

import android.content.Context
import com.gifft.gift.GiftRepositoryImpl
import com.gifft.gift.MyObjectBox
import com.gifft.gift.api.GiftRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module(
    includes = [
        GiftModule.Declarations::class
    ]
)
internal object GiftModule {

    @Module
    interface Declarations {
        @Binds
        fun GiftRepositoryImpl.bindGiftRepositoryImpl(): GiftRepository
    }

    @Provides
    @Singleton
    fun provideBoxStore(context: Context): BoxStore =
        MyObjectBox.builder().androidContext(context).build()
}
