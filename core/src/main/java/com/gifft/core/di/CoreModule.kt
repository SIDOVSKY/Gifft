package com.gifft.core.di

import android.content.Context
import com.gifft.core.api.gift.GiftRepository
import com.gifft.core.gift.GiftRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        CoreModule.Declarations::class
    ]
)
internal object CoreModule {

    @Module
    interface Declarations {
        @Binds
        fun GiftRepositoryImpl.bindGiftRepositoryImpl(): GiftRepository
    }
}
