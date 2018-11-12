package com.nayagadi.android.onboarding

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OnBoardingModule {

    @Provides
    fun provideCreateAccountRepository(auth: FirebaseAuth) : CreateAccountRepository{
        return CreateAccountRepository(auth)
    }

    @Provides
    fun provideAccountModelFactory(repository: CreateAccountRepository) : AccountViewModelFactory {
        return AccountViewModelFactory(repository)
    }

}
