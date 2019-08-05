package com.blocksdecoded.dex.utils.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun BigDecimal.toDisplayFormat(): String = CurrencyUtils.df.format(this)
fun Double.toDisplayFormat(): String = CurrencyUtils.df.format(this)
fun Double.toFiatDisplayFormat(): String = CurrencyUtils.formatDoubleString(this)

object CurrencyUtils {
    val df = DecimalFormat("#,##0.00####")

    private val fiatFormat = DecimalFormat("#,###.00")
    private val smallFiatFormat = DecimalFormat("#,##0.00####")

    fun formatDoubleString(double: Double): String =
        if (double < 10f) {
            smallFiatFormat.format(double)
        } else {
            fiatFormat.format(double)
        }
}