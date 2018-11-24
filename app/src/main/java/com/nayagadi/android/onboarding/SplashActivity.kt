package com.nayagadi.android.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.R
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

private val RC_SIGN_IN = 100

class SplashActivity : BaseActivity() {
    override fun getActionBarTitle(): Int {
        return -1
    }

    lateinit var viewPager: ViewPager


    override fun getLayoutId(): Int = R.layout.activity_splash


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val onBoardingModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)
        val onBoardingDetails = onBoardingModel.getOnboardingDetails(this.applicationContext)

        tutorial_pager.adapter = customAdapter(this.applicationContext, onBoardingDetails)

        btn_create_account.setOnClickListener {
            // Choose authentication providers
            val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build()
                    )

//            // Create and launch sign-in intent
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    RC_SIGN_IN)

//            createAccountActivity(this)
            createAccountDetailsActivity(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}

class customAdapter(val context: Context, val onboardingDetails: Array<OnBoarding>) : PagerAdapter() {

    override fun getCount() = 3

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.layout_pager, collection, false) as ViewGroup
        layout.findViewById<TextView>(R.id.onboarding_title).text = onboardingDetails[position].title
        layout.findViewById<TextView>(R.id.onboarding_body).text = onboardingDetails[position].body
        layout.findViewById<ImageView>(R.id.onboarding_image).setImageDrawable(context.getDrawable(onboardingDetails[position].resId))
        collection.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}
