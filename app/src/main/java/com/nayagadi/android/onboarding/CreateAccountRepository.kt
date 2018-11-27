package com.nayagadi.android.onboarding

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import io.reactivex.Observable

const val AGENTS = "agents"
const val CUSTOMERS = "customers"

class CreateAccountRepository(private val auth: FirebaseAuth, private val database: DatabaseReference) {


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

    fun loginUser(activity: Activity, email: String, pwd: String): io.reactivex.Observable<FirebaseUser> {
        return io.reactivex.Observable.create<FirebaseUser> { emitter ->
            if (!emitter.isDisposed) {
                auth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.getCurrentUser()
                                user?.let { user ->
                                    emitter.onNext(user)
                                    emitter.onComplete()
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

    fun loginUser(activity: Activity): io.reactivex.Observable<FirebaseUser> {
//        return io.reactivex.Observable.create<FirebaseUser> { emitter ->
//            if (!emitter.isDisposed) {
//                auth.signInWithEmailAndPassword (email, pwd)
//                        .addOnCompleteListener(activity) { task ->
//                            if (task.isSuccessful) {
//                                // Sign in success, update UI with the signed-in user's information
//                                val user = auth.getCurrentUser()
//                                user?.let { user ->
//                                    emitter.onNext(user)
//                                    emitter.onComplete()
//                                } ?: run {
//                                    emitter.onError(Throwable("USer does not exit!!"))
//                                }
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                emitter.onError(task.exception ?: Throwable("Unknow error"))
//                            }
//                        }
//            }
//        }
        TODO("Implementation Pending!!")
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

    fun updateProfile(userId: String, agent: Agent): Observable<Agent> {
        return io.reactivex.Observable.create<Agent> { emitter ->
            if (!emitter.isDisposed) {
                database.child(AGENTS).child(userId).setValue(agent)
                        .addOnCompleteListener {
                            emitter.onNext(agent)
                            emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it)
                        }
                        .addOnCanceledListener {
                            emitter.onError(Throwable("Update profile cancelled"))
                        }
            }
        }
    }

}
