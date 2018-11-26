package com.nayagadi.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nayagadi.android.utils.hide
import com.nayagadi.android.utils.show
import kotlinx.android.synthetic.main.layout_tool_bar.*
import kotlinx.android.synthetic.main.layout_tool_bar.view.*

abstract class BaseActivity : AppCompatActivity() {

    var intentBundle: Bundle? = null

    lateinit var nayagadiApplication: NayagadiApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentBundle = fetchIntentBundle(intent)

        setContentView(getLayoutId())

        nayagadiApplication = application as NayagadiApplication

        supportActionBar?.hide()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (getActionBarTitle() != -1) {
            toolbar_nayagadi?.app_bar_title?.text = resources.getString(getActionBarTitle())
        } else {
            toolbar_nayagadi?.app_bar_title?.text = resources.getString(R.string.app_name)
        }
        if (showAppBarBackButton()) {
            app_bar_back_pressed?.show()
        } else {
            app_bar_back_pressed?.hide()
        }
        app_bar_back_pressed?.setOnClickListener { onBackPressed() }
    }

    abstract fun getActionBarTitle(): Int

    abstract fun getLayoutId(): Int

    abstract fun fetchIntentBundle(intent: Intent): Bundle?

    open fun showAppBarBackButton() = true
}
