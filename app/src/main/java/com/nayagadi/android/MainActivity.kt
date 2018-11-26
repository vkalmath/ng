package com.nayagadi.android

import android.content.Intent

class MainActivity : BaseActivity() {
    override fun getActionBarTitle(): Int {
        return -1
    }

    override fun fetchIntentBundle(intent: Intent) = intent.extras

    override fun getLayoutId() = R.layout.activity_main


}
