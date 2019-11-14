package com.fridaytech.dex.utils.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

fun BigDecimal.toDisplayFormat(): String = NumberUtils.df.format(this)
fun BigDecimal.toLongDisplayFormat(): String = NumberUtils.longDf.format(this)
fun Double.toDisplayFormat(): String = NumberUtils.df.format(this)
fun BigDecimal.toMediumDisplayFormat(): String = NumberUtils.mediumDf.format(this)

fun BigDecimal.toPriceFormat(): String =
    NumberUtils.formatCoinPrice(this)
fun BigDecimal.toPercentFormat(): String = NumberUtils.fiatFormat.format(this)
fun Double.toFiatDisplayFormat(): String =
    NumberUtils.formatDoubleFiat(this)
fun BigDecimal.toFiatDisplayFormat(): String =
    NumberUtils.formatBigDecimalFiat(this)

object NumberUtils {
    fun withSuffix(count: Double): String {
        if (count < 1000) return "" + count
        val exp = (ln(count) / ln(1000.0)).toInt()
        return String.format("%.1f %c",
            count / 1000.0.pow(exp.toDouble()),
            "kmbtpe"[exp - 1])
    }

    fun withSuffix(amount: BigDecimal): String {
        if (amount < BigDecimal(1000)) return "" + amount
        val exp = (ln(amount.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c",
            amount / BigDecimal(1000).pow(exp),
            "kmbtpe"[exp - 1])
    }

    val df = DecimalFormat("#,##0.00##")
    val longDf = DecimalFormat("#,##0.00#######")
    val mediumDf = DecimalFormat("#,##0.00####")
    val thousandFormat = DecimalFormat("#,##0.#")

    val fiatFormat = DecimalFormat("#,##0.00")
    val smallFiatFormat = DecimalFormat("#,##0.00###")

    fun formatCoinPrice(value: BigDecimal): String =
        when {
            value < BigDecimal.TEN -> mediumDf.format(value)

            else -> thousandFormat.format(value)
        }

    fun formatBigDecimalFiat(value: BigDecimal): String =
        if (value < BigDecimal.ONE) {
            smallFiatFormat.format(value)
        } else {
            fiatFormat.format(value)
        }

    fun formatDoubleFiat(double: Double): String =
        if (double < 1.0) {
            smallFiatFormat.format(double)
        } else {
            fiatFormat.format(double)
        }
}
