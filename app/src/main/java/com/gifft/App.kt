package com.gifft

import com.gifft.di.ApiProviderAggregation
import com.gifft.di.CommonsComponent
import com.google.firebase.FirebaseApp

/**
 *  Inherits [ApiProviderAggregation] to provide dependencies to modules' components via cast:
 *  ```
 *  DaggerSomeModuleComponent.builder()
 *      .dependency(app as Dependency)
 *      .build()
 *  ```
 */
@Suppress("unused")
class App : AppWithReferenceToSelf(),
    ApiProviderAggregation by CommonsComponent.create(app = self) {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
