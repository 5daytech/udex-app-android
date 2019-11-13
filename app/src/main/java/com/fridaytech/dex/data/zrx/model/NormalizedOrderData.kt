package com.fridaytech.dex.data.zrx.model

import com.blocksdecoded.zrxkit.model.SignedOrder
import com.fridaytech.dex.core.model.Coin
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
