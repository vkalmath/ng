package com.nayagadi.android

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)
}
