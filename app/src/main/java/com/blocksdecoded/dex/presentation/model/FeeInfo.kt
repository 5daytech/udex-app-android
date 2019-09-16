package com.blocksdecoded.dex.presentation.model

import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

data class FeeInfo(
    val coin: Coin,
    val amount: BigDecimal,
    val fiatAmount: BigDecimal,
    val error: Int
)