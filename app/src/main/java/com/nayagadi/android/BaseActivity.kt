package com.nayagadi.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View

abstract class BaseActivity : AppCompatActivity() {

    lateinit var nayagadiApplication: NayagadiApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutId())

        nayagadiApplication = application as NayagadiApplication

    }


    abstract fun getLayoutId(): Int
}
