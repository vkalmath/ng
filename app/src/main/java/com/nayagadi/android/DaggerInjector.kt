package com.nayagadi.android

import android.app.Application

class DaggerInjector {

    /**
     * CompanionObject
     * Setting DaggerAppComponent and DataModule
     * @link https://github.com/google/dagger
     */
    companion object {
        var appComponent: AppComponent? = null
    }

}
