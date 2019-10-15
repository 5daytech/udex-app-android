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
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.utils.ui.AnimationHelper
import com.blocksdecoded.dex.utils.ui.DimenUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.synthetic.main.view_market_order.view.*


fun <T>List<T>?.isValidIndex(index: Int): Boolean = index in 0 until (this?.size ?: 0)

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

fun View?.removeFocus() = try {
    val imm = this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    imm?.hideSoftInputFromWindow(this?.windowToken, 0)
} catch (e: Exception) {
    Logger.e(e)
}

fun View.showKeyboard(toggleKeyboard: Boolean = true) {
    try {
        if (context != null && context is Activity) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            if (imm is InputMethodManager) {
                this.requestFocus()
                if (toggleKeyboard) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
                } else {
                    imm.showSoftInput(this, 0)
                }
            }
        }
    } catch (e: Exception) {
        Logger.e(e)
    }
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

fun Context.openTransactionUrl(transactionHash: String) {
    openUrl("${App.appConfiguration.transactionExploreBaseUrl}$transactionHash")
}

fun Context.openUrl(url: String) {
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(this, Uri.parse(url))
}

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

fun View.dp(value: Float): Int = DimenUtils.dp(value, context)

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

fun TextView.bindFiatAmountInfo(
    info: AmountInfo,
    @AttrRes textColor: Int = R.attr.SecondaryHintTextColor,
    @StringRes textRes: Int
) {
    val hintColor = context.theme.getAttr(textColor) ?: 0
    val errorColor = ContextCompat.getColor(context, R.color.red)

    val hintInputColor = if (info.error == 0) hintColor else errorColor

    this.setTextColor(hintInputColor)

    if (info.error == 0) {
        this.text = context.getString(textRes, info.value.toFiatDisplayFormat())
    } else {
        this.setText(info.error)
    }
}