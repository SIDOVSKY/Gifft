package com.gifft.di

import android.app.Application
import com.gifft.core.di.AppApiProvider
import dagger.Component
import javax.inject.Singleton
import dagger.BindsInstance

@Singleton
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent : AppApiProvider {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }

    companion object {
        fun create(app: Application): AppComponent =
            DaggerAppComponent
                .factory()
                .create(app)
    }
}
