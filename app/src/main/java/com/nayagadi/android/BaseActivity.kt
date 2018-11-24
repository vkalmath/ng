package com.nayagadi.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_tool_bar.*

abstract class BaseActivity : AppCompatActivity() {

    lateinit var nayagadiApplication: NayagadiApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutId())

        nayagadiApplication = application as NayagadiApplication

        supportActionBar?.hide()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if(getActionBarTitle() != -1) {
            toolbar_nayagadi?.title = resources.getString(getActionBarTitle())
        } else {
            toolbar_nayagadi?.title = resources.getString(R.string.app_name)
        }
        app_bar_back_pressed?.setOnClickListener { onBackPressed() }

    }

    abstract fun getActionBarTitle(): Int

    abstract fun getLayoutId(): Int
}
