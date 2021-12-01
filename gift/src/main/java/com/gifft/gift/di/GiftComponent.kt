package com.gifft.gift.di

import android.app.Application
import com.gifft.core.di.AppApiProvider
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
        fun create(app: Application): GiftComponent = DaggerGiftComponent.builder()
            .appApiProvider(app as AppApiProvider)
            .build()
    }
}
