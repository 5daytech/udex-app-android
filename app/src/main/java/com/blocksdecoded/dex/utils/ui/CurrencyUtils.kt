package com.blocksdecoded.dex.utils.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.ln

fun BigDecimal.toDisplayFormat(): String = CurrencyUtils.df.format(this).replace(",", " ")
fun BigDecimal.toLongDisplayFormat(): String = CurrencyUtils.longDf.format(this).replace(",", " ")
fun Double.toDisplayFormat(): String = CurrencyUtils.df.format(this).replace(",", " ")
fun BigDecimal.toMediumDisplayFormat(): String = CurrencyUtils.mediumDf.format(this).replace(",", " ")

fun BigDecimal.toPriceFormat(): String = CurrencyUtils.formatCoinPrice(this).replace(",", " ")
fun Double.toFiatDisplayFormat(): String = CurrencyUtils.formatDoubleFiat(this).replace(",", " ")
fun BigDecimal.toFiatDisplayFormat(): String = CurrencyUtils.formatBigDecimalFiat(this).replace(",", " ")

object CurrencyUtils {
    fun withSuffix(count: Float): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c",
            count / Math.pow(1000.0, exp.toDouble()),
            "kmbtpe"[exp - 1])
    }

    val df = DecimalFormat("#,##0.00##")
    val longDf = DecimalFormat("#,##0.00#######")
    val mediumDf = DecimalFormat("#,##0.00####")
    val thousandFormat = DecimalFormat("#,##0.#")

    private val fiatFormat = DecimalFormat("#,###.00")
    private val smallFiatFormat = DecimalFormat("#,##0.00###")

    fun formatCoinPrice(value: BigDecimal): String =
        when {
            value < BigDecimal.TEN -> mediumDf.format(value)

            else -> thousandFormat.format(value)
        }

    fun formatBigDecimalFiat(value: BigDecimal): String =
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