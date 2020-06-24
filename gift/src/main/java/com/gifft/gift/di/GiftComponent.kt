package com.gifft.gift.di

import com.gifft.core.api.di.AppApiProvider
import com.gifft.gift.api.di.GiftApiProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        GiftModule::class
    ],
    dependencies = [
        AppApiProvider::class
    ]
)
interface GiftComponent : GiftApiProvider {
    companion object {
        fun create(appApiProvider: AppApiProvider): GiftComponent = DaggerGiftComponent.builder()
            .appApiProvider(appApiProvider)
            .build()
    }
}
