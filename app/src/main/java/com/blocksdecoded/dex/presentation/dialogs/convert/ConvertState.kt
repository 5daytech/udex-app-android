package com.blocksdecoded.dex.presentation.dialogs.convert

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import java.math.BigDecimal

data class ConvertState (
    val fromCoin: Coin,
    val toCoin: Coin,
    val balance: TotalBalanceInfo,
    val type: ConvertConfig.ConvertType
)