package com.blocksdecoded.dex.presentation.dialogs.convert

import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

data class ConvertState (
    val fromCoin: Coin,
    val toCoin: Coin,
    val balance: BigDecimal,
    val type: ConvertConfig.ConvertType
)