package com.nayagadi.android.utils

import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.nayagadi.android.R

fun Button.enable(enable: Boolean) {
    this.isClickable = enable
    this.background = if (enable) {
        ContextCompat.getDrawable(this.context, R.drawable.round_chip_red)
    } else {
        ContextCompat.getDrawable(this.context, R.drawable.round_button_gray)
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun String.isValidIFSC() : Boolean {
    return this.matches(Regex("^[a-zA-Z]{4}[0-9]{7}")) && this.length == 11
}

fun String.isValidPhone() : Boolean {
    return this.matches(Regex("^[0-9]{10}")) && this.length == 10
}

fun String.isValidName() : Boolean {
    return this.matches(Regex("^[a-zA-Z].*")) && this.length > 1 && this.length < 25
}

fun String.isValidAccountNumber() : Boolean {
    return this.matches(Regex("^[0-9]{10}")) && this.length == 10
}

fun String.isValidPinCode() : Boolean {
    return this.matches(Regex("^[0-9]{6}")) && this.length == 6
}





