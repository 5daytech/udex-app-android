package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.CoinRate
import java.math.BigDecimal

class RatesConverter(
	private val baseCoinCode: String = "ETH",
	private val ratesManager: IRatesManager
) {
	private val baseCoins = listOf("BTC", "ETH")

	private fun getCoinRate(code: String): CoinRate {
		if (code.isEmpty()) return CoinRate(code)

		val baseIndex = baseCoins.indexOfFirst { it == code.substring(1) }
		return if (baseIndex >= 0) {
			ratesManager.getRate(baseCoins[baseIndex])
		} else {
			ratesManager.getRate(code)
		}
	}

	fun getCoinDiff(base: String, quote: String): BigDecimal {
		val baseRate = getCoinRate(base)
		val fromRate = getCoinRate(quote)

		return if (baseRate.price == 0.0 || fromRate.price == 0.0) {
			BigDecimal.ZERO
		} else {
			(baseRate.price / fromRate.price).toBigDecimal()
		}
	}
	
	fun getTokenPrice(code: String): Double = getCoinRate(code).price
	
	fun baseFrom(code: String): Double {
		val baseRate = getCoinRate(baseCoinCode)
		val fromRate = getCoinRate(code)
		
		return if (baseRate.price == 0.0 || fromRate.price == 0.0) {
			0.0
		} else {
			fromRate.price / baseRate.price
		}
	}
	
	fun getCoinsPrice(code: String, amount: BigDecimal): BigDecimal {
		val rate = getCoinRate(code)
		
		return amount.multiply(BigDecimal(rate.price))
	}
}