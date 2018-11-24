package com.nayagadi.android

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackbar(view: View, stringId: Int, duration: Int = Snackbar.LENGTH_SHORT,
                 actionId: Int,
                 listener: () -> Unit)  {
    val snackBar = Snackbar.make(view, stringId, duration)
    snackBar.setAction(actionId) {
        listener()
        snackBar.dismiss()
    }
    snackBar.show()
}
