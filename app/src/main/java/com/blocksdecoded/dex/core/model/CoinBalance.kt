package com.blocksdecoded.dex.core.model

import java.math.BigDecimal

data class CoinBalance(
	val coin: Coin,
	val balance: BigDecimal,
	val fiatBalance: BigDecimal,
	val pricePerToken: BigDecimal,
	val convertType: EConvertType
)

enum class EConvertType {
	NONE,
	WRAP,
	UNWRAP
}
