package com.blocksdecoded.dex.presentation.markets

import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

data class MarketViewItem(
    val coin: Coin,
    var price: BigDecimal,
    var change: BigDecimal,
    var marketCap: BigDecimal
)
