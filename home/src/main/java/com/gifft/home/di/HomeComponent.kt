package com.gifft.home.di

import android.app.Application
import com.gifft.core.api.di.CoreApiProvider
import com.gifft.home.HomeActivity
import com.gifft.wrapping.api.di.WrappingApiProvider
import dagger.Component

@Component(
    dependencies = [
        CoreApiProvider::class,
        WrappingApiProvider::class
    ]
)
interface HomeComponent {
    companion object {
        fun create(app: Application): HomeComponent = DaggerHomeComponent.builder()
            .coreApiProvider(app as CoreApiProvider)
            .wrappingApiProvider(app as WrappingApiProvider)
            .build()
    }

    fun inject(homeActivity: HomeActivity)
}
