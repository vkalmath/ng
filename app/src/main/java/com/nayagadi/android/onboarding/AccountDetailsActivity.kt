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
import com.nayagadi.android.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.update_profile_layout.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


const val USER_ID = "USER_ID"
fun createAccountDetailsActivity(context: Context, userId: String) {
    val intent = Intent(context, AccountDetailsActivity::class.java)
    val bundle = Bundle()
    bundle.putString(USER_ID, userId)
    intent.putExtras(bundle)
    context.startActivity(intent)
}

class AccountDetailsActivity : BaseActivity() {
    var userId: String? = null

    override fun fetchIntentBundle(intent: Intent) = intent.extras

    override fun showAppBarBackButton() = false

    @Inject
    lateinit var accountViewModelFactory: AccountViewModelFactory

    override fun getActionBarTitle(): Int = R.string.account_info

    override fun getLayoutId(): Int = R.layout.update_profile_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nayagadiApplication.createOnBoardingComponent()
        NayagadiApplication.onBoardingComponent?.inject(this)

        showSnackbar(btn_update_profile, R.string.verfiy_account, Snackbar.LENGTH_SHORT, R.string.verfiy_account_action) {
            val emailLauncher = Intent(Intent.ACTION_VIEW)
            emailLauncher.type = "message/rfc822"
            try {
                startActivity(emailLauncher)
            } catch (e: Throwable) {
                Toast.makeText(applicationContext, "Check you email Inbox and click on the link sen to you to verify the account.", Toast.LENGTH_LONG).show()
            }
        }

        userId = intentBundle?.getString(USER_ID)
        Timber.d("userId = ${userId}")

        btn_update_profile.enable(false)

        btn_update_profile.setOnClickListener { view ->
            val createAccountViewModel = accountViewModelFactory.create(CreateAccountViewModel::class.java)

            val agent = Agent(edit_first_name.text.toString(),
                    edit_last_name.text.toString(),
                    edit_phone_number.text.toString(),
                    edit_account_number.text.toString(),
                    ifsc_code.text.toString(),
                    edit_city.text.toString(),
                    spinner_state.selectedItem.toString(),
                    edit_pin_code.text.toString())
            userId?.let {
                createAccountViewModel.updateProfile(it, agent)
                        .startWith(ProfileLoadingState)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<ProfileState>() {
                            override fun onComplete() {
                                progressar_creation.hide()
                                dispose()
                            }

                            override fun onNext(state: ProfileState) {
                                when (state) {
                                    is ProfileLoadingState -> {
                                        progressar_creation.show()
                                    }

                                    is ProfileSuccessState -> {
                                        progressar_creation.hide()
                                        Toast.makeText(this@AccountDetailsActivity, "Profile Saved, NAvigate to Home Activity!!",
                                                Toast.LENGTH_LONG).show()
                                        //todo: naviagte to home activity
                                        createHomeActivity(this@AccountDetailsActivity)
                                        finish()
                                    }

                                    is ProfileErrorState -> {
                                        progressar_creation.hide()
                                        Toast.makeText(this@AccountDetailsActivity, "Profile Save failed with Error ${state.error?.localizedMessage}",
                                                Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                progressar_creation.hide()
                                Toast.makeText(this@AccountDetailsActivity,
                                        "Failed Updating profile with error ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }

                        })
            } ?: run {
                Toast.makeText(this, "userID can not be null!!", Toast.LENGTH_LONG).show()
            }


        }
        hideAllEditTextErrors()

        edit_first_name.textChanges()
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
                        if (!t.toString().isValidName()) {
                            textview_valid_fn.show()
                        } else {
                            textview_valid_fn.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })


        edit_last_name.textChanges()
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
                        if (!t.toString().isValidName()) {
                            textview_valid_ln.show()
                        } else {
                            textview_valid_ln.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_phone_number.textChanges()
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
                        if (!t.toString().isValidPhone()) {
                            textview_valid_phone.show()
                        } else {
                            textview_valid_phone.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_account_number.textChanges()
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
                        if (!t.toString().isValidAccountNumber()) {
                            textview_valid_acct.show()
                        } else {
                            textview_valid_acct.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        ifsc_code.textChanges()
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
                        if (!t.toString().isValidIFSC()) {
                            textview_valid_ifsc.show()
                        } else {
                            textview_valid_ifsc.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_city.textChanges()
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
                        if (!t.toString().isValidName()) {
                            textview_valid_city.show()
                        } else {
                            textview_valid_city.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        edit_pin_code.textChanges()
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
                        if (!t.toString().isValidPinCode()) {
                            textview_valid_pin_code.show()
                        } else {
                            textview_valid_pin_code.hide()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                    }

                })

        val firstNameLasntName = Observable.combineLatest(edit_first_name.textChanges(), edit_last_name.textChanges(),
                object : io.reactivex.functions.BiFunction<CharSequence, CharSequence, Boolean> {
                    override fun apply(fn: CharSequence, ln: CharSequence): Boolean {
                        return fn.toString().isValidName() && ln.toString().isValidName()
                    }
                }
        )

        val phoneNumberAccountNumer = Observable.combineLatest(edit_phone_number.textChanges(), edit_account_number.textChanges(),
                object : io.reactivex.functions.BiFunction<CharSequence, CharSequence, Boolean> {
                    override fun apply(phone: CharSequence, account: CharSequence): Boolean {
                        return phone.toString().isValidPhone() && account.toString().isValidAccountNumber()
                    }
                }
        )

        val phAcctIfsc = Observable.combineLatest(phoneNumberAccountNumer, ifsc_code.textChanges(),
                object : io.reactivex.functions.BiFunction<Boolean, CharSequence, Boolean> {
                    override fun apply(validPhoneAndAccount: Boolean, ifsc: CharSequence): Boolean {
                        return validPhoneAndAccount && ifsc.toString().isValidIFSC()
                    }
                }
        )



        Observable.combineLatest(firstNameLasntName, phAcctIfsc,
                object : io.reactivex.functions.BiFunction<Boolean, Boolean, Boolean> {
                    override fun apply(firstLAst: Boolean, account: Boolean): Boolean {
                        return firstLAst && account
                    }
                }
        )
                .startWith(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ResourceObserver<Boolean>() {
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onNext(isValid: Boolean) {
                        btn_update_profile.enable(isValid)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e.localizedMessage)
                        dispose()
                    }
                })

        spinner_state.setSelection(0)

    }


    private fun hideAllEditTextErrors() {
        textview_valid_ifsc.hide()
        textview_valid_acct.hide()
        textview_valid_phone.hide()
        textview_valid_ln.hide()
        textview_valid_fn.hide()
        textview_valid_city.hide()
        textview_valid_pin_code.hide()
        textview_valid_state.hide()
    }


    override fun onDestroy() {
        super.onDestroy()

        nayagadiApplication.releaseOnBoardingComponent()
    }
}
