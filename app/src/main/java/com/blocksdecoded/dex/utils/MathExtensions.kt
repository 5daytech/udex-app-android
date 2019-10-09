package com.blocksdecoded.dex.utils

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
    precision: Int = 8,
    roundingMode: RoundingMode = RoundingMode.FLOOR
): BigDecimal {
    return this.divide(divisor, precision, roundingMode)
        .stripTrailingZeros()
}