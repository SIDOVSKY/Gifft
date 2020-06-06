package com.gifft.core.di

import com.gifft.core.api.di.AppApiProvider
import com.gifft.core.api.gift.GiftRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class
    ],
    dependencies = [
        AppApiProvider::class
    ]
)
interface CoreComponent {
    companion object {
        fun create(appApiProvider: AppApiProvider): CoreComponent = DaggerCoreComponent.builder()
            .appApiProvider(appApiProvider)
            .build()
    }

    fun provideGiftRepository(): GiftRepository
}
