package com.gifft.di

import com.gifft.core.api.di.AppApiProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent : AppApiProvider {
    companion object {
        fun create(appModule: AppModule): AppComponent =
            DaggerAppComponent
                .builder()
                .appModule(appModule)
                .build()
    }
}
