package com.nayagadi.android

import android.app.Application

class NayagadiApplication : Application() {

    companion object {
        var appComponent: AppComponent? = null
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .build()
    }
}
