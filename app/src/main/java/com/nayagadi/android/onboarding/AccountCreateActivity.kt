package com.nayagadi.android.onboarding

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.widget.textChanges
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.NayagadiApplication
import com.nayagadi.android.R
import com.nayagadi.android.showSnackbar
import com.nayagadi.android.utils.enable
import com.nayagadi.android.utils.hide
import com.nayagadi.android.utils.show
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.create_account_layout.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


fun createAccountActivity(context: Context) {
    val intent = Intent(context, AccountCreateActivity::class.java)
    context.startActivity(intent)
}

class AccountCreateActivity : BaseActivity() {

    override fun getActionBarTitle() =  R.string.create_account

    @Inject
    lateinit var accountViewModelFactory: AccountViewModelFactory

    override fun getLayoutId(): Int = R.layout.create_account_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        nayagadiApplication.createOnBoardingComponent()
        NayagadiApplication.onBoardingComponent?.inject(this)

        btn_update.setOnClickListener {
            val createAccountViewModel = accountViewModelFactory.create(CreateAccountViewModel::class.java)

            createAccountViewModel.createUserWithEmailAndPassword(this, edit_email.text.toString(), edit_pwd.text.toString())
                    .subscribeWith(object : ResourceObserver<AccountState>() {
                        override fun onComplete() {
                            dispose()
                        }

                        override fun onNext(state: AccountState) {
                            when (state) {
                                is AccountCreateSuccessState -> {
                                    Timber.e("Firebase USer -> ${state.user}")
                                    Toast.makeText(applicationContext, "Account created successfully!", Toast.LENGTH_LONG)
                                            .show()
                                    progressar_creation.hide()

                                    createAccountDetailsActivity(this@AccountCreateActivity)
                                    finish()

                                    showSnackbar(btn_update, R.string.verfiy_account, Snackbar.LENGTH_INDEFINITE, R.string.verfiy_account_action) {
                                        val emailLauncher = Intent(Intent.ACTION_VIEW)
                                        emailLauncher.type = "message/rfc822"
                                        try {
                                            startActivity(emailLauncher)
                                        } catch (e: ActivityNotFoundException) {
                                            Toast.makeText(applicationContext, "Check you email Inbox and click on the link sen to you to verify the account.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                is AccountCreationErrorState -> {
                                    showError(state.error ?: Throwable("Empty error!!"))
                                }

                                is AccountCreationLoadingState -> {
                                    progressar_creation.show()
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            showError(e)
                            dispose()
                        }

                    })
        }

        edit_email.textChanges()
                .doOnNext {
                    textview_valid_email.hide()
                    progressar_creation.hide()
                    textview_error.hide()
                }
                .debounce(2000, TimeUnit.MILLISECONDS)
                .filter {
                    !TextUtils.isEmpty(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : ResourceObserver<CharSequence>() {
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onNext(t: CharSequence) {
                        if (!isValidEmail(t)) {
                            textview_valid_email.show()
                        } else {
                            textview_valid_email.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_pwd.textChanges()
                .doOnNext {
                    textview_pwd_valid.hide()
                    progressar_creation.hide()
                    textview_error.hide()
                }
                .debounce(2000, TimeUnit.MILLISECONDS)
                .filter {
                    !TextUtils.isEmpty(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : ResourceObserver<CharSequence>() {
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onNext(t: CharSequence) {
                        if (!isValidPwd(t)) {
                            textview_pwd_valid.show()
                        } else {
                            textview_pwd_valid.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                        dispose()
                    }

                })

        Observable.combineLatest(edit_pwd.textChanges(), edit_email.textChanges(), object : io.reactivex.functions.BiFunction<CharSequence, CharSequence, Boolean> {
            override fun apply(pwd: CharSequence, email: CharSequence): Boolean {
                return isValidEmail(email) && isValidPwd(pwd)
            }
        })
                .startWith(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ResourceObserver<Boolean>() {
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onNext(isValid: Boolean) {
                        btn_update.enable(isValid)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                        dispose()
                    }
                })

    }

    private fun showError(error: Throwable) {
        Timber.e(error?.localizedMessage)
        progressar_creation.hide()
        textview_error.show()
        textview_error.text = error?.localizedMessage
    }

    override fun onDestroy() {
        super.onDestroy()

        nayagadiApplication.releaseOnBoardingComponent()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) false else android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPwd(target: CharSequence?): Boolean {
        return if (target == null) false else target.length > 6
    }
}
