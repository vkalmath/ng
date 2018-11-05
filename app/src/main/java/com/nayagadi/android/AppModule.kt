package com.nayagadi.android

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private var application: NayagadiApplication) {

    @Singleton
    @Provides
    fun provideApplicationContext() = application.applicationContext

    @Singleton
    @Provides
    fun provideApplication() = application
}
