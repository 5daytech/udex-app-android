package com.blocksdecoded.dex.core.model

import java.math.BigDecimal

data class CoinValue(
	val coin: Coin,
	val balance: BigDecimal,
	val fiatBalance: BigDecimal,
	val pricePerToken: Double,
	val convertType: EConvertType
)

enum class EConvertType {
	NONE,
	WRAP,
	UNWRAP
}
