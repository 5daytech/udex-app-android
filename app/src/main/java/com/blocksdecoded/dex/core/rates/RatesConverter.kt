package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.Market
import java.math.BigDecimal

class RatesConverter(
	private val baseCoinCode: String = "ETH",
	private val ratesManager: IRatesManager
) {
	private fun getCoinRate(code: String): Market {
		if (code.isEmpty()) return Market(code)

		return ratesManager.getMarket(code)
	}

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