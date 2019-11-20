package com.fridaytech.dex.utils

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.models.AmountInfo
import com.fridaytech.dex.utils.ui.AnimationHelper
import com.fridaytech.dex.utils.ui.DimenUtils
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.fridaytech.dex.utils.ui.toPercentFormat
import java.math.BigDecimal

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
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

fun View.setVisible(visible: Boolean, animated: Boolean = false, animationSpeed: Float = 1f) {
    if (!animated) {
        this.visible = visible
        return
    }

    if (visible) {
        AnimationHelper.expand(this, animationSpeed)
    } else {
        AnimationHelper.collapse(this, animationSpeed)
    }
}

fun View.dp(value: Float): Int = DimenUtils.dp(value, context)

fun View.setMargins(newLeft: Int, newTop: Int, newRight: Int, newBottom: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(newLeft, newTop, newRight, newBottom)
        this.layoutParams = it
    }
}

fun View.setTopMargin(top: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.topMargin = top
        this.layoutParams = it
    }
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attach)

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(this.context, colorRes))
}

fun TextView?.bindChangePercent(change: BigDecimal, withSign: Boolean = true) {
    val isPositive = change >= BigDecimal.ZERO
    val sign = when {
        !withSign -> ""
        isPositive -> "+"
        else -> "-"
    }
    this?.setTextColorRes(if (isPositive) R.color.green else R.color.red)
    this?.text = "$sign${change.abs().toPercentFormat()}%"
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
