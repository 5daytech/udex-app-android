package com.blocksdecoded.dex.presentation.markets

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.Market

data class MarketViewItem(
    val coin: Coin,
    val market: Market
)