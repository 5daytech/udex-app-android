package com.blocksdecoded.dex.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.utils.ui.AnimationHelper



fun <T>List<T>?.isValidIndex(index: Int): Boolean = index in 0 until (this?.size ?: 0)

fun Activity?.showKeyboard() {
    this?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}

fun Activity?.hideKeyboard() {
    this?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
}

fun View?.removeFocus() = try {
    val imm = this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    imm?.hideSoftInputFromWindow(this?.windowToken, 0)
} catch (e: Exception) {

}

val Context.screenSize: Point
    get() = Point().apply {
        val wm = getSystemService(Context.WINDOW_SERVICE)
        if (wm is WindowManager) {
            wm.defaultDisplay.getSize(this)
        }
    }

fun Context.openTransactionUrl(transactionHash: String) {
    openUrl("${App.appConfiguration.transactionExploreBaseUrl}$transactionHash")
}

fun Context.openUrl(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, Uri.parse(url))
}

val Context.screenHeight
    get() = screenSize.y

val Context.screenWidth
    get() = screenSize.x

fun View.setVisible(visible: Boolean, animated: Boolean = false) {
    if (!animated) {
        this.visible = visible
        return
    }

    if (visible) {
        AnimationHelper.expand(this)
    } else {
        AnimationHelper.collapse(this)
    }
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attach)

val Fragment.currentFocus : View?
    get() = activity?.window?.currentFocus

fun Resources.Theme.getAttr(attr: Int): Int? {
    val typedValue = TypedValue()
    return if (resolveAttribute(attr, typedValue, true))
        typedValue.data
    else
        null
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(this.context, colorRes))
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }