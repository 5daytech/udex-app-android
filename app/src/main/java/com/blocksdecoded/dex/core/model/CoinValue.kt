package com.blocksdecoded.dex.core.model

import java.math.BigDecimal

data class CoinValue(
        val coin: Coin,
        val value: BigDecimal
)
