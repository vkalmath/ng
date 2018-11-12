package com.nayagadi.android.onboarding

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Observable
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider



class CreateAccountViewModel(private val repository: CreateAccountRepository): ViewModel() {

    fun createUserWithEmailAndPassword(activity: Activity, email: String, pwd: String) : Observable<FirebaseUser> {
        return repository.createUser(activity, email, pwd)

    }
}

class AccountViewModelFactory  @Inject constructor(val repository: CreateAccountRepository) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateAccountViewModel(repository) as T
    }
}




