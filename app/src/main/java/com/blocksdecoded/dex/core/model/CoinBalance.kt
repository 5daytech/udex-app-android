package com.blocksdecoded.dex.core.model

import java.math.BigDecimal

data class CoinBalance(
    val coin: Coin,
    val balance: BigDecimal,
    val fiatBalance: BigDecimal,
    val pricePerToken: BigDecimal,
    val state: BalanceState,
    val convertType: EConvertType
)

enum class BalanceState {
    SYNCED,
    SYNCING,
    FAILED
}

enum class EConvertType {
    NONE,
    WRAP,
    UNWRAP
}
