package com.gifft.core.api.di

import android.content.Context
import android.content.SharedPreferences

interface AppApiProvider {
    fun provideContext(): Context

    fun provideSharedPreferences(): SharedPreferences
}
