package com.blocksdecoded.dex.utils.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun BigDecimal.toDisplayFormat(): String = CurrencyUtils.df.format(this)
fun BigDecimal.toLongDisplayFormat(): String = CurrencyUtils.longDf.format(this)
fun Double.toDisplayFormat(): String = CurrencyUtils.df.format(this)

fun Double.toFiatDisplayFormat(): String = CurrencyUtils.formatDoubleFiat(this)
fun BigDecimal.toFiatDisplayFormat(): String = CurrencyUtils.formatBigDecimalFial(this)

object CurrencyUtils {
    val df = DecimalFormat("#,##0.00####")
    val longDf = DecimalFormat("#,##0.00########")

    private val fiatFormat = DecimalFormat("#,###.00")
    private val smallFiatFormat = DecimalFormat("#,##0.00####")
    
    fun formatBigDecimalFial(value: BigDecimal): String =
        if (value < BigDecimal.TEN) {
            smallFiatFormat.format(value)
        } else {
            fiatFormat.format(value)
        }
    
    fun formatDoubleFiat(double: Double): String =
        if (double < 10.0) {
            smallFiatFormat.format(double)
        } else {
            fiatFormat.format(double)
        }
}