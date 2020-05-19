package com.gifft.di

import com.gifft.core.api.di.AppApiProvider
import com.gifft.core.api.di.CoreApiProvider
import com.gifft.core.di.CoreCommonsModule
import com.gifft.home.di.HomeCommonsModule
import com.gifft.wrapping.api.di.WrappingApiProvider
import com.gifft.wrapping.di.WrappingCommonsModule
import dagger.Component
import javax.inject.Singleton

// In the same file for the ease of adding new modules' DI APIs
interface ApiProviderAggregation :
    AppApiProvider,
    CoreApiProvider,
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
        AppCommonsModule::class,
        CoreCommonsModule::class,
        HomeCommonsModule::class,
        WrappingCommonsModule::class
    ]
)
interface CommonsComponent : ApiProviderAggregation {

    companion object {
        fun create(appCommonsModule: AppCommonsModule): CommonsComponent =
            DaggerCommonsComponent
                .builder()
                .appCommonsModule(appCommonsModule)
                .build()
    }
}
