package com.blocksdecoded.dex.utils.ui

import android.content.Context
import com.blocksdecoded.dex.App
import kotlin.math.ceil

object DimenUtils {
    fun dp(dp: Float, context: Context? = App.instance) = context?.let {
        val density = context.resources.displayMetrics.density
        if (dp == 0f) 0 else ceil((density * dp).toDouble()).toInt()
    } ?: dp.toInt()
}
