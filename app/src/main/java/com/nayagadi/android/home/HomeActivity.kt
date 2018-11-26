package com.nayagadi.android.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.R

fun createHomeActivity(activity: AppCompatActivity) {
    val intent = Intent(activity, HomeActivity::class.java)
    activity.startActivity(intent)
}


class HomeActivity : BaseActivity() {
    override fun getActionBarTitle(): Int = -1

    override fun getLayoutId() = R.layout.activity_home

    override fun fetchIntentBundle(intent: Intent): Bundle? {
        //todo
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showAppBarBackButton() = false
}
