package com.fridaytech.dex.presentation.orders.model

import java.math.BigDecimal

data class FillOrderInfo(
    val pair: Pair<String, String>,
    val amount: BigDecimal,
    val side: EOrderSide
)
