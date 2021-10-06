package com.gifft.di

import android.app.Application
import com.gifft.core.api.di.AppApiProvider
import com.gifft.core.api.di.CoreApiProvider
import com.gifft.core.api.di.CoreCommonsModule
import com.gifft.gift.api.di.GiftApiProvider
import com.gifft.gift.di.GiftComponent
import com.gifft.home.di.HomeCommonsModule
import com.gifft.unwrapping.di.UnwrappingCommonsModule
import com.gifft.wrapping.api.di.WrappingApiProvider
import com.gifft.wrapping.di.WrappingCommonsModule
import dagger.Component
import javax.inject.Singleton

// In the same file for the ease of adding new modules' DI APIs
interface ApiProviderAggregation :
    AppApiProvider,
    CoreApiProvider,
    GiftApiProvider,
    WrappingApiProvider

/**
 *  Inherits modules' api providers to be inherited by the application class
 *  and provision them to other modules via cast:
 *  ```
 *  DaggerSomeModuleComponent.builder()
 *      .dependency(app as Dependency)
 *      .build()
 *  ```
 *  and avoid
 *  1. messy property access like:
 *    ```
 *    .dependency((application as AppWithDependencyFacade).getFacade())
 *    ```
 *  2. changes in common `AppWithDependencyFacade`
 *  when adding new module api provider will lead to rebuilding of all modules
 */
@Singleton
@Component(
    modules = [
        CoreCommonsModule::class,
        HomeCommonsModule::class,
        WrappingCommonsModule::class,
        UnwrappingCommonsModule::class,
    ],
    dependencies = [
        AppApiProvider::class,
        GiftApiProvider::class,
    ]
)
interface CommonsComponent : ApiProviderAggregation {

    companion object {
        fun create(app: Application): CommonsComponent {
            return DaggerCommonsComponent
                .builder()
                .appApiProvider(AppComponent.create(app))
                .giftApiProvider(GiftComponent.create(app))
                .build()
        }
    }
}
