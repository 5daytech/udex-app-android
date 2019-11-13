package com.fridaytech.dex.presentation.markets

import com.fridaytech.dex.core.model.Coin
import java.math.BigDecimal

data class MarketViewItem(
    val coin: Coin,
    var price: BigDecimal,
    var change: BigDecimal,
    var marketCap: Double
)
