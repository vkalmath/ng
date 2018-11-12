package com.nayagadi.android

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
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

    @Singleton
    @Provides
    fun provideFireaseAuthInstance() = FirebaseAuth.getInstance()
}
