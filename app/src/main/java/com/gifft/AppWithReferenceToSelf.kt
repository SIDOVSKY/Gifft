package com.gifft

import android.app.Application

/**
 * Provides reference to self in a companion object property to let subclasses
 * use it before full construction e. g. during interface delegation setup.
 */
@Suppress("LeakingThis")
open class AppWithReferenceToSelf : Application() {
    companion object {
        /**
         * Caution! This object may not be fully constructed here so its members shouldn't be used!
         *
         * It must be used just as a reference for the future use.
         */
        @JvmStatic
        protected lateinit var self: Application
    }

    init {
        self = this
    }
}
