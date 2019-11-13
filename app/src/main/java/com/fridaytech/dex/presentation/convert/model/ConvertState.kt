package com.fridaytech.dex.presentation.convert.model

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.model.EConvertType
import com.fridaytech.dex.presentation.widgets.balance.TotalBalanceInfo

data class ConvertState(
    val fromCoin: Coin,
    val toCoin: Coin,
    val balance: TotalBalanceInfo,
    val type: EConvertType
)
