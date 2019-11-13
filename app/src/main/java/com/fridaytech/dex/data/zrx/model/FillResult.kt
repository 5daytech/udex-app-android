package com.fridaytech.dex.data.zrx.model

import com.blocksdecoded.zrxkit.model.SignedOrder
import java.math.BigDecimal

data class FillResult(
    val orders: List<SignedOrder>,
    val receiveAmount: BigDecimal,
    val sendAmount: BigDecimal
) {
    companion object {
        fun empty(): FillResult =
            FillResult(listOf(), BigDecimal.ZERO, BigDecimal.ZERO)
    }
}
