package com.blocksdecoded.dex.core.rates

import com.blocksdecoded.dex.core.model.CoinRate
import java.math.BigDecimal

class RatesConverter(
	private val baseCode: String = "ETH",
	private val ratesManager: IRatesManager
) {
	private fun getCoinRate(code: String): CoinRate = if (code.contains(baseCode)) {
		ratesManager.getRate(baseCode)
	} else {
		ratesManager.getRate(code)
	}
	
	fun baseFrom(code: String): Double {
		val baseRate = getCoinRate(baseCode)
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