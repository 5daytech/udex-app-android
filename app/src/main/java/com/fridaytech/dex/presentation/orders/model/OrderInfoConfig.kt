package com.fridaytech.dex.presentation.orders.model

import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.model.SignedOrder

data class OrderInfoConfig(
    val order: SignedOrder,
    val info: OrderInfo,
    val side: EOrderSide
)
