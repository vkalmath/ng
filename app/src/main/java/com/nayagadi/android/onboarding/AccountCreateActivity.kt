package com.nayagadi.android.onboarding

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
import com.nayagadi.android.home.createHomeActivity
import com.nayagadi.android.showSnackbar
import com.nayagadi.android.utils.enable
import com.nayagadi.android.utils.gone
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

const val LOGIN_OR_CREATE_ACCOUNT = "LoginOrCreateAccount"

fun createAccountActivity(context: Context, isLogin: Boolean = false) {
    val intent = Intent(context, AccountCreateActivity::class.java)
    val bundle = Bundle()
    bundle.putBoolean(LOGIN_OR_CREATE_ACCOUNT, isLogin)
    intent.putExtras(bundle)
    context.startActivity(intent)
}



class AccountCreateActivity() : BaseActivity(){
    override fun fetchIntentBundle(intent: Intent): Bundle? {
        return intent.extras
    }

    fun isLogin(bundle: Bundle?) : Boolean {
        return bundle?.getBoolean(LOGIN_OR_CREATE_ACCOUNT) ?: false
    }

    var isLogin: Boolean = false

    override fun getActionBarTitle() : Int {
        isLogin = isLogin(intentBundle)
        if(isLogin) {
            btn_update.text = getString(R.string.signin)
            btn_forgot_pwd.show()
            return R.string.signin
        } else {
            btn_forgot_pwd.gone()
            return R.string.create_account
        }
    }

    @Inject
    lateinit var accountViewModelFactory: AccountViewModelFactory

    override fun getLayoutId(): Int = R.layout.create_account_layout

    override fun showAppBarBackButton() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nayagadiApplication.createOnBoardingComponent()
        NayagadiApplication.onBoardingComponent?.inject(this)

        btn_update.setOnClickListener {
            val createAccountViewModel = accountViewModelFactory.create(CreateAccountViewModel::class.java)

            val observable = if(!isLogin) {
                createAccountViewModel.createUserWithEmailAndPassword(this, edit_email.text.toString(), edit_pwd.text.toString())
            } else {
                createAccountViewModel.loginWithEmailAndPassword(this, edit_email.text.toString(), edit_pwd.text.toString())
            }
            observable
                    .subscribeWith(object : ResourceObserver<AccountState>() {
                        override fun onComplete() {
                            dispose()
                        }

                        override fun onNext(state: AccountState) {
                            when (state) {
                                is AccountSuccessState -> {
                                    Timber.e("Firebase USer -> ${state.user}")
                                    Toast.makeText(applicationContext, "Account created successfully!", Toast.LENGTH_LONG)
                                            .show()
                                    progressar_creation.hide()

                                    if(!isLogin) {
                                        //todo: storing emailID as key check it later
                                        createAccountDetailsActivity(this@AccountCreateActivity, state.user?.email!!)

                                        finish()
                                    } else {
                                        Toast.makeText(this@AccountCreateActivity, "Lauch Home activity", Toast.LENGTH_LONG).show()
                                        //todo: naviagte to home activity
                                        createHomeActivity(this@AccountCreateActivity)
                                        finish()
                                    }


                                }

                                is AccountErrorState -> {
                                    showError(state.error ?: Throwable("Empty error!!"))
                                }

                                is AccountLoadingState -> {
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

        btn_forgot_pwd.setOnClickListener {
            Toast.makeText(this, "Launch PAssowrd forgot activity", Toast.LENGTH_LONG).show()
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
        Timber.e(error.localizedMessage)
        progressar_creation.hide()
        textview_error.show()
        textview_error.text = error.localizedMessage
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
