package com.fridaytech.dex.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

fun BigInteger.toDecimals(decimals: Int): BigDecimal {
    return this.toBigDecimal()
}

fun BigInteger.fromDecimals(decimals: Int): BigDecimal {
    return this.toBigDecimal()
}

fun BigDecimal.normalizedDiv(
    divisor: BigDecimal,
    precision: Int = 18,
    roundingMode: RoundingMode = RoundingMode.UP
): BigDecimal {
    return if (divisor == BigDecimal.ZERO)
        BigDecimal.ZERO
    else
        this.divide(divisor, precision, roundingMode)
            .stripTrailingZeros()
}

fun BigDecimal.normalizedMul(
    multiplicand: BigDecimal,
    precision: Int = 18,
    roundingMode: RoundingMode = RoundingMode.UP
): BigDecimal {
    val mc = MathContext(precision, roundingMode)

    return this.multiply(multiplicand, mc)
        .setScale(precision, roundingMode)
        .stripTrailingZeros()
}
