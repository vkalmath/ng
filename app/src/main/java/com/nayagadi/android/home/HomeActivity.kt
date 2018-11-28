package com.nayagadi.android.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nayagadi.android.BaseActivity
import com.nayagadi.android.R
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import android.graphics.Color.parseColor
import android.view.View
import androidx.core.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import kotlinx.android.synthetic.main.activity_home.*


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

        bottom_navigation.setColoredModeColors(ContextCompat.getColor(applicationContext, R.color.colorLightBlue), ContextCompat.getColor(applicationContext, R.color.colorDarkGray))
        // Create items
        val item1 = AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_home, R.color.colorWhite)
        val item2 = AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_like, R.color.colorWhite)
        val item3 = AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_settings, R.color.colorWhite)

        // Add items
        bottom_navigation.addItem(item1)
        bottom_navigation.addItem(item2)
        bottom_navigation.addItem(item3)

        // Disable the translation inside the CoordinatorLayout
        //  bottomNavigation.isBehaviorTranslationEnabled = false
        // Manage titles
        bottom_navigation.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE

        // Use colored navigation with circle reveal effect
        bottom_navigation.isColored = true

    }

    override fun showAppBarBackButton() = false
}
