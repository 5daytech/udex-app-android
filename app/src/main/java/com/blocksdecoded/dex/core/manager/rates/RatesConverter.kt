package com.blocksdecoded.dex.core.manager.rates

import com.blocksdecoded.dex.core.model.Rate
import java.math.BigDecimal
import java.util.*

class RatesConverter(
	private val baseCoinCode: String = "ETH",
	private val ratesManager: IRatesManager
) {
	private fun getCoinRate(code: String): Rate =
		ratesManager.getLatestRate(code) ?: Rate(code, Date().time / 1000, BigDecimal.ZERO)

	fun getCoinDiff(base: String, quote: String): BigDecimal {
		val baseRate = getCoinRate(base)
		val fromRate = getCoinRate(quote)

		return if (baseRate.price == BigDecimal.ZERO || fromRate.price == BigDecimal.ZERO) {
			BigDecimal.ZERO
		} else {
			(baseRate.price / fromRate.price)
		}
	}
	
	fun getTokenPrice(code: String): BigDecimal = getCoinRate(code).price
	
	fun baseFrom(code: String): BigDecimal {
		val baseRate = getCoinRate(baseCoinCode)
		val fromRate = getCoinRate(code)
		
		return if (baseRate.price == BigDecimal.ZERO || fromRate.price == BigDecimal.ZERO) {
			BigDecimal.ZERO
		} else {
			fromRate.price / baseRate.price
		}
	}
	
	fun getCoinsPrice(code: String, amount: BigDecimal): BigDecimal {
		val rate = getCoinRate(code)
		
		return amount.multiply(rate.price)
	}
}