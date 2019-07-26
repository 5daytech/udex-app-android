package com.blocksdecoded.dex.core.utils

import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast

fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .apply { setGravity(Gravity.TOP, 0, screenHeight / 2) }
            .show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG)
            .apply { setGravity(Gravity.TOP, 0, screenHeight / 2) }
            .show()
}

val Context.screenSize: Point
    get() = Point().apply {
        val wm = getSystemService(Context.WINDOW_SERVICE)
        if (wm is WindowManager) {
            wm.defaultDisplay.getSize(this)
        }
    }

val Context.screenHeight
    get() = screenSize.y

val Context.screenWidth
    get() = screenSize.x

fun View.setVisible(visible: Boolean, animated: Boolean = false) {
    this.visible = visible
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }