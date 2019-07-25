package com.blocksdecoded.dex.core.utils

import java.math.BigDecimal
import java.text.DecimalFormat

fun BigDecimal.toDisplayFormat(): String = CurrencyUtils.df.format(this)
fun Double.toDisplayFormat(): String = CurrencyUtils.df.format(this)

object CurrencyUtils {
    val df = DecimalFormat("#,##0.00####")
}