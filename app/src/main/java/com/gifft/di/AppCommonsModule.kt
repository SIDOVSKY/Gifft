package com.gifft.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.gifft.core.api.di.AppApiProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppCommonsModule {
    companion object {
        private const val PREFS_NAME = "GIFFT_PREFS"
    }

    lateinit var application: Application

    @Provides
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
