package com.blocksdecoded.dex.core.manager.zrx.model

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.zrxkit.model.SignedOrder
import java.math.BigDecimal

data class NormalizedOrderData(
    val orderHash: String,
    val makerCoin: Coin,
    val takerCoin: Coin,
    val makerAmount: BigDecimal,
    val takerAmount: BigDecimal,
    val price: BigDecimal,
    val order: SignedOrder?
)