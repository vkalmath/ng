package com.nayagadi.android.onboarding

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class CreateAccountRepository(private val auth : FirebaseAuth) {


    fun createUser(activity: Activity, email: String, pwd: String) : io.reactivex.Observable<FirebaseUser>{
        return io.reactivex.Observable.create<FirebaseUser> {
            if(!it.isDisposed) {
                auth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.getCurrentUser()
                                it.onNext(user)
                                it.onComplete()
                            } else {
                                // If sign in fails, display a message to the user.
                                it.onError(task.exception)
                            }
                        }
            }
        }
    }

}
