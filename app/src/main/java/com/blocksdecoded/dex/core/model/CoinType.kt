package com.blocksdecoded.dex.core.model

import java.io.Serializable
import java.math.BigDecimal

sealed class CoinType : Serializable {
    object Ethereum : CoinType()
    class Erc20(val address: String, val decimal: Int, val fee: BigDecimal = BigDecimal.ZERO) : CoinType()
}
