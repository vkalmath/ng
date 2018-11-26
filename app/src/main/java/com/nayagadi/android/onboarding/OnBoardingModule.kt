package com.nayagadi.android.onboarding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OnBoardingModule {

    @Provides
    fun provideCreateAccountRepository(auth: FirebaseAuth, databaseRef: DatabaseReference): CreateAccountRepository {
        return CreateAccountRepository(auth, databaseRef)
    }

    @Provides
    fun provideAccountModelFactory(repository: CreateAccountRepository): AccountViewModelFactory {
        return AccountViewModelFactory(repository)
    }

}
