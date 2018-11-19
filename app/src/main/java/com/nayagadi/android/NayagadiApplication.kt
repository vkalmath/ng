package com.nayagadi.android

import android.app.Application
import com.nayagadi.android.onboarding.OnBoardingComponent
import com.nayagadi.android.onboarding.OnBoardingModule
import timber.log.Timber

class NayagadiApplication : Application() {

    companion object {
        var appComponent: AppComponent? = null
        var onBoardingComponent : OnBoardingComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        createAppComponent()

        Timber.plant(Timber.DebugTree())
    }

    fun createAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    fun createOnBoardingComponent() {
        onBoardingComponent = appComponent?.plus(OnBoardingModule())
    }

    fun releaseOnBoardingComponent() {
        onBoardingComponent = null
    }
}
