package com.nayagadi.android.onboarding

import dagger.Subcomponent

@OnBoardingScope
@Subcomponent(modules = [OnBoardingModule::class])
interface OnBoardingComponent {
    fun inject(accountCreateActivity: AccountCreateActivity)
    fun inject(accountCreateActivity: AccountDetailsActivity)
}
