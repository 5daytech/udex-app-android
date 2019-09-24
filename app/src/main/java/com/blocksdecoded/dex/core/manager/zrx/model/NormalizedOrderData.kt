package com.blocksdecoded.dex.core.manager.zrx.model

import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

data class NormalizedOrderData(
    val makerCoin: Coin,
    val takerCoin: Coin,
    val makerAmount: BigDecimal,
    val takerAmount: BigDecimal,
    val price: BigDecimal
)