package com.nayagadi.android.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.widget.textChanges
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.NayagadiApplication
import com.nayagadi.android.R
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

    @Inject
    lateinit var accountViewModelFactory: AccountViewModelFactory

    override fun getLayoutId(): Int = R.layout.create_account_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.app_name)

        nayagadiApplication.createOnBoardingComponent()
        NayagadiApplication.onBoardingComponent?.inject(this)

        btn_create.setOnClickListener {
            val createAccountViewModel = accountViewModelFactory.create(CreateAccountViewModel::class.java)

            createAccountViewModel.createUserWithEmailAndPassword(this, edit_email.text.toString(), edit_pwd.text.toString())
                    .subscribeWith(object : ResourceObserver<AccountCreateState>() {
                        override fun onComplete() {
                            dispose()
                        }

                        override fun onNext(state: AccountCreateState) {
                            when (state) {
                                is AccountCreateSuccessState -> {
                                    Timber.e("Firebase USer -> ${state.user}")
                                    Toast.makeText(applicationContext, "Account created successfully!", Toast.LENGTH_LONG)
                                            .show()
                                    progressar_creation.visibility = View.INVISIBLE
                                }

                                is AccountCreationErrorState -> {
                                    showError(state.error ?: Throwable("Empty error!!"))
                                }

                                is AccountCreationLoadingState -> {
                                    progressar_creation.visibility = View.VISIBLE
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
                    textview_valid_email.visibility = View.INVISIBLE
                    progressar_creation.visibility = View.INVISIBLE
                    textview_error.visibility = View.INVISIBLE
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
                            textview_valid_email.visibility = View.VISIBLE
                        } else {
                            textview_valid_email.visibility = View.INVISIBLE
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_pwd.textChanges()
                .doOnNext {
                    textview_pwd_valid.visibility = View.INVISIBLE
                    progressar_creation.visibility = View.INVISIBLE
                    textview_error.visibility = View.INVISIBLE
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
                            textview_pwd_valid.visibility = View.VISIBLE
                        } else {
                            textview_pwd_valid.visibility = View.INVISIBLE
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
                        enableCreateButton(isValid)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                        dispose()
                    }
                })

    }

    private fun showError(error: Throwable) {
        Timber.e(error?.localizedMessage)
        progressar_creation.visibility = View.INVISIBLE
        textview_error.visibility = View.VISIBLE
        textview_error.text = error?.localizedMessage
    }

    private fun enableCreateButton(isValid: Boolean) {
        btn_create.isClickable = isValid
        btn_create.background = if (isValid) {
            ContextCompat.getDrawable(applicationContext, R.drawable.round_chip_blue)
        } else {
            ContextCompat.getDrawable(applicationContext, R.drawable.round_button_gray)
        }
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
