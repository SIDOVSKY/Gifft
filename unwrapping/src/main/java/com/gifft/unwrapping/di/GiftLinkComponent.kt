package com.gifft.unwrapping.di

import android.app.Application
import com.gifft.core.di.CoreApiProvider
import com.gifft.unwrapping.GiftLinkActivity
import dagger.Component

@Component(
    dependencies = [
        CoreApiProvider::class
    ]
)
interface GiftLinkComponent {
    companion object {
        fun create(app: Application): GiftLinkComponent = DaggerGiftLinkComponent.builder()
            .coreApiProvider(app as CoreApiProvider)
            .build()
    }

    fun inject(giftLinkActivity: GiftLinkActivity)
}
