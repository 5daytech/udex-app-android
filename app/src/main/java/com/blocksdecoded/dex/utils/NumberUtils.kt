package com.blocksdecoded.dex.utils

import java.math.BigDecimal
import java.math.BigInteger

fun BigInteger.toDecimals(decimals: Int): BigDecimal {
    return this.toBigDecimal()
}

fun BigInteger.fromDecimals(decimals: Int): BigDecimal {
    return this.toBigDecimal()
}