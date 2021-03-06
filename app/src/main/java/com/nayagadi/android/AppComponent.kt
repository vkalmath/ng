package com.nayagadi.android

import com.nayagadi.android.onboarding.AccountCreateActivity
import com.nayagadi.android.onboarding.OnBoardingComponent
import com.nayagadi.android.onboarding.OnBoardingModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)

    fun plus(onBoardingModule: OnBoardingModule) : OnBoardingComponent
}
