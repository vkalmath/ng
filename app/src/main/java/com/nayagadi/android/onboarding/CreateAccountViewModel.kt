package com.nayagadi.android.onboarding

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider


sealed class AccountState(val user: FirebaseUser?, val loading: Boolean = false, val error: Throwable? = null)

class AccountCreateSuccessState(user: FirebaseUser) : AccountState(user)

object AccountCreationLoadingState : AccountState(null, true, null)

class AccountCreationErrorState(error: Throwable) : AccountState(null, false, error)

class CreateAccountViewModel(private val repository: CreateAccountRepository): ViewModel() {

    fun createUserWithEmailAndPassword(activity: Activity, email: String, pwd: String) : Observable<AccountState> {
        return repository.createUser(activity, email, pwd)
                .map<AccountState> {
                    AccountCreateSuccessState(it)
                }
                .startWith(AccountCreationLoadingState)
                .onErrorReturn {
                    AccountCreationErrorState(it)
                }
    }

    fun getCurrentUser() : Observable<AccountState> {
        return repository.getCurrentUser()
                .map<AccountState> {
                    AccountCreateSuccessState(it)
                }
                .onErrorReturn {
                    AccountCreationErrorState(it)
                }
    }
}

class AccountViewModelFactory  @Inject constructor(val repository: CreateAccountRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateAccountViewModel(repository) as T
    }
}




