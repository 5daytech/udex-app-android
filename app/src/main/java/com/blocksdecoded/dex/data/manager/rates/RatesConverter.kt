package com.blocksdecoded.dex.data.manager.rates

import com.blocksdecoded.dex.utils.normalizedDiv
import com.blocksdecoded.dex.utils.normalizedMul
import java.math.BigDecimal

class RatesConverter(
    private val baseCoinCode: String = "ETH",
    private val ratesManager: IRatesManager
) {
    private fun getCoinRate(code: String): BigDecimal =
        ratesManager.getLatestRate(code) ?: BigDecimal.ZERO

    fun getCoinDiff(base: String, quote: String): BigDecimal {
        val baseRate = getCoinRate(base)
        val fromRate = getCoinRate(quote)

        return if (baseRate == BigDecimal.ZERO || fromRate == BigDecimal.ZERO) {
            BigDecimal.ZERO
        } else {
            baseRate.normalizedDiv(fromRate)
        }
    }

    fun getCoinPrice(code: String): BigDecimal = getCoinRate(code)

    fun baseFrom(code: String): BigDecimal {
        val baseRate = getCoinRate(baseCoinCode)
        val fromRate = getCoinRate(code)

        return if (baseRate == BigDecimal.ZERO || fromRate == BigDecimal.ZERO) {
            BigDecimal.ZERO
        } else {
            fromRate.normalizedDiv(baseRate)
        }
    }

    fun getCoinsPrice(code: String, amount: BigDecimal): BigDecimal {
        val rate = getCoinRate(code)

        return amount.normalizedMul(rate)
    }
}
