package com.nayagadi.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutId())

        val actionbar = findViewById<View>(getActionBarId())

        actionbar?.let {
            setSupportActionBar(it as Toolbar)
        }
    }

    abstract fun getActionBarId(): Int

    abstract fun getLayoutId(): Int
}
