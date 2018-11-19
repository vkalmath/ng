package com.nayagadi.android.onboarding

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider


sealed class AccountCreateState(val user: FirebaseUser?, val loading: Boolean = false, val error: Throwable? = null)

class AccountCreateSuccessState(user: FirebaseUser) : AccountCreateState(user)

object AccountCreationLoadingState : AccountCreateState(null, true, null)

class AccountCreationErrorState(error: Throwable) : AccountCreateState(null, false, error)

class CreateAccountViewModel(private val repository: CreateAccountRepository): ViewModel() {

    fun createUserWithEmailAndPassword(activity: Activity, email: String, pwd: String) : Observable<AccountCreateState> {
        return repository.createUser(activity, email, pwd)
                .map<AccountCreateState> {
                    AccountCreateSuccessState(it)
                }
                .startWith(AccountCreationLoadingState)
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




