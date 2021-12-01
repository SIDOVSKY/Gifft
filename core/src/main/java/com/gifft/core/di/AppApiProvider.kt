package com.gifft.core.di

import android.content.Context
import android.content.SharedPreferences

interface AppApiProvider {
    fun provideContext(): Context

    fun provideSharedPreferences(): SharedPreferences
}
