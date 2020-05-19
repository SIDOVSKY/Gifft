package com.gifft

import android.app.Application
import com.gifft.di.ApiProviderAggregation
import com.gifft.di.AppCommonsModule
import com.gifft.di.CommonsComponent

/**
 *  Inherits [ApiProviderAggregation] to provide dependencies to modules' components via cast:
 *  ```
 *  DaggerSomeModuleComponent.builder()
 *      .dependency(app as Dependency)
 *      .build()
 *  ```
 */
@Suppress("unused")
class App : Application(),
    ApiProviderAggregation by CommonsComponent.create(appCommonsModule) {

    companion object {
        private val appCommonsModule = AppCommonsModule()
    }

    init {
        appCommonsModule.application = this
    }
}
