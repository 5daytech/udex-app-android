package com.blocksdecoded.dex.presentation.widgets.balance

import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

data class TotalBalanceInfo(
    val coin: Coin,
    val balance: BigDecimal,
    val fiatBalance: BigDecimal
)