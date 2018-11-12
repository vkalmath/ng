package com.nayagadi.android

import android.app.Application
import com.nayagadi.android.onboarding.OnBoardingModule

class NayagadiApplication : Application() {

    companion object {
        var appComponent: AppComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .onBoardingModule(OnBoardingModule())
                .build()
    }
}
