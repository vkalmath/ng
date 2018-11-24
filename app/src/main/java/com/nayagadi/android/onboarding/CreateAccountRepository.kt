package com.nayagadi.android.onboarding

import android.app.Activity
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class CreateAccountRepository(private val auth: FirebaseAuth) {


    fun createUser(activity: Activity, email: String, pwd: String): io.reactivex.Observable<FirebaseUser> {
        return io.reactivex.Observable.create<FirebaseUser> { emitter ->
            if (!emitter.isDisposed) {
                auth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.getCurrentUser()
                                user?.let { user ->
                                    user.sendEmailVerification()
                                            .addOnCompleteListener {
                                                emitter.onNext(user)
                                                emitter.onComplete()
                                            }
                                } ?: run {
                                    emitter.onError(Throwable("USer does not exit!!"))
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                emitter.onError(task.exception ?: Throwable("Unknow error"))
                            }
                        }
            }
        }
    }

    fun getCurrentUser(): io.reactivex.Observable<FirebaseUser> {
        return io.reactivex.Observable.create<FirebaseUser> { emitter ->
            if (!emitter.isDisposed) {
                auth.addAuthStateListener { fireaseAuth ->
                    val user = auth.getCurrentUser()
                    user?.let {
                        emitter.onNext(user)
                        emitter.onComplete()
                    } ?: run {
                        emitter.onError(Throwable("User does not exit!!"))
                    }
                }
            }
        }
    }

}
