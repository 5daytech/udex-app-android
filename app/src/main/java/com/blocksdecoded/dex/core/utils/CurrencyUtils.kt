package com.blocksdecoded.dex.core.utils

import java.math.BigDecimal
import java.text.DecimalFormat

fun BigDecimal.format() = CurrencyUtils.df.format(this)
fun Double.format() = CurrencyUtils.df.format(this)

object CurrencyUtils {
    val df = DecimalFormat("#,##0.######")
}