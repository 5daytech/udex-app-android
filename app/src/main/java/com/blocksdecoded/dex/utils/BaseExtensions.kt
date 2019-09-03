package com.blocksdecoded.dex.utils

import android.content.Context
import android.graphics.Point
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blocksdecoded.dex.utils.ui.AnimationHelper
import java.math.BigDecimal

fun <T>List<T>?.isValidIndex(index: Int): Boolean = index in 0 until (this?.size ?: 0)

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

fun TextView.setColoredAmount(amount: BigDecimal) {

}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(this.context, colorRes))
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }