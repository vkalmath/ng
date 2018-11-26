package com.nayagadi.android.onboarding

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider


sealed class AccountState(val user: FirebaseUser?, val loading: Boolean = false, val error: Throwable? = null)

class AccountSuccessState(user: FirebaseUser) : AccountState(user)

object AccountLoadingState : AccountState(null, true, null)

class AccountErrorState(error: Throwable) : AccountState(null, false, error)

sealed class ProfileState(val agent: Agent?, val loading: Boolean = false, val error: Throwable? = null)

class ProfileSuccessState(agent: Agent) : ProfileState(agent)

object ProfileLoadingState : ProfileState(null, true, null)

class ProfileErrorState(error: Throwable) : ProfileState(null, false, error)

class CreateAccountViewModel(private val repository: CreateAccountRepository): ViewModel() {

    fun createUserWithEmailAndPassword(activity: Activity, email: String, pwd: String) : Observable<AccountState> {
        return repository.createUser(activity, email, pwd)
                .map<AccountState> {
                    AccountSuccessState(it)
                }
                .startWith(AccountLoadingState)
                .onErrorReturn {
                    AccountErrorState(it)
                }
    }

    fun loginWithEmailAndPassword(activity: Activity, email: String, pwd: String) : Observable<AccountState> {
        return repository.loginUser(activity, email, pwd)
                .map<AccountState> {
                    AccountSuccessState(it)
                }
                .startWith(AccountLoadingState)
                .onErrorReturn {
                    AccountErrorState(it)
                }
    }

    fun getCurrentUser() : Observable<AccountState> {
        return repository.getCurrentUser()
                .map<AccountState> {
                    AccountSuccessState(it)
                }
                .onErrorReturn {
                    AccountErrorState(it)
                }
    }

    fun updateProfile(userId: String, agent: Agent): Observable<ProfileState> {
        return repository.updateProfile(userId, agent)
                .map<ProfileState> {
                    ProfileSuccessState(it)
                }
                .startWith(ProfileLoadingState)
                .onErrorReturn {
                    ProfileErrorState(it)
                }
    }
}

class AccountViewModelFactory  @Inject constructor(val repository: CreateAccountRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateAccountViewModel(repository) as T
    }
}




