package com.fridaytech.dex.presentation.widgets.balance

import com.fridaytech.dex.core.model.Coin
import java.math.BigDecimal

data class TotalBalanceInfo(
    val coin: Coin,
    var balance: BigDecimal,
    var fiatBalance: BigDecimal
)
