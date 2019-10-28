package com.blocksdecoded.dex.core.manager.zrx.model

import com.blocksdecoded.zrxkit.model.IOrder
import java.math.BigDecimal

data class FillResult(
    val orders: List<IOrder>,
    val receiveAmount: BigDecimal,
    val sendAmount: BigDecimal
) {
    companion object {
        fun empty(): FillResult = FillResult(listOf(), BigDecimal.ZERO, BigDecimal.ZERO)
    }
}