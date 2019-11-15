package com.fridaytech.dex.data.zrx.model

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.zrxkit.model.SignedOrder
import java.math.BigDecimal

data class NormalizedOrderData(
    val orderHash: String,
    val makerCoin: Coin,
    val takerCoin: Coin,
    val makerAmount: BigDecimal,
    val remainingMakerAmount: BigDecimal,
    val takerAmount: BigDecimal,
    val remainingTakerAmount: BigDecimal,
    val price: BigDecimal,
    val order: SignedOrder?
)
