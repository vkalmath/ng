package com.nayagadi.android.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.data.model.Resource
import com.google.firebase.auth.FirebaseUser
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.NayagadiApplication
import com.nayagadi.android.R
import io.reactivex.observers.ResourceObserver
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.create_account_layout.*
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

        NayagadiApplication.appComponent?.inject(this)

        btn_create.setOnClickListener {
            val createAccountViewModel = accountViewModelFactory.create(CreateAccountViewModel::class.java)

            createAccountViewModel.createUserWithEmailAndPassword(this, edit_email.text.toString(), edit_pwd.text.toString())
                    .subscribeWith(object: ResourceObserver<FirebaseUser>() {
                        override fun onComplete() {
                            dispose()
                        }

                        override fun onNext(value: FirebaseUser?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onError(e: Throwable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })
        }
    }
}
