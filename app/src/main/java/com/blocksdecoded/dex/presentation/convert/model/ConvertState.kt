package com.blocksdecoded.dex.presentation.convert.model

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo

data class ConvertState (
    val fromCoin: Coin,
    val toCoin: Coin,
    val balance: TotalBalanceInfo,
    val type: ConvertConfig.ConvertType
)