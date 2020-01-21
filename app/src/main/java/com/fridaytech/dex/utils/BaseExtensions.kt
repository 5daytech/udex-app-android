package com.fridaytech.dex.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fridaytech.dex.App

fun <T> List<T>?.isValidIndex(index: Int): Boolean = index in 0 until (this?.size ?: 0)

fun Activity?.showKeyboard() {
    this?.window?.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}

fun Activity?.hideKeyboard() = try {
    this?.let {
        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE)
        if (imm is InputMethodManager && this.currentFocus != null) {
            imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        }
    }
} catch (e: Exception) {
    Logger.e(e)
}

fun Context.openTransactionUrl(transactionHash: String) {
    openUrl("${App.appConfiguration.transactionExploreBaseUrl}$transactionHash")
}

fun Context.openUrl(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, Uri.parse(url))
}

val Fragment.currentFocus: View?
    get() = activity?.window?.currentFocus

fun Resources.Theme.getAttr(attr: Int): Int? {
    val typedValue = TypedValue()
    return if (resolveAttribute(attr, typedValue, true))
        typedValue.data
    else
        null
}

//region Resources

val Context.density
    get() = resources.displayMetrics.density

val Context.scaledDensity
    get() = resources.displayMetrics.scaledDensity

val Context.screenHeight
    get() = screenSize.y

val Context.screenWidth
    get() = screenSize.x

val Context.screenSize: Point
    get() = Point().apply {
        val wm = getSystemService(Context.WINDOW_SERVICE)
        if (wm is WindowManager) {
            wm.defaultDisplay.getSize(this)
        }
    }

val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0)
            resources.getDimensionPixelSize(resourceId)
        else
            dpToPx(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25)
    }

val Context.navBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

fun Context.dpToPx(dp: Int) = (dp * density).toInt()

fun Context.pxToDp(px: Int) = (px / density).toInt()

fun Context.pxToSp(px: Float) = px / scaledDensity

fun Context.spToPx(sp: Float) = sp * scaledDensity

fun Context.getColorRes(@ColorRes color: Int): Int = ContextCompat.getColor(this, color)

fun Fragment.getColorRes(@ColorRes color: Int): Int = context?.let {
    ContextCompat.getColor(it, color)
} ?: Color.BLACK

val Activity.isTranslucentStatus: Boolean
    get() {
        val w = this.window
        val lp = w.attributes
        val flags = lp.flags
        return flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    }

//endregion
