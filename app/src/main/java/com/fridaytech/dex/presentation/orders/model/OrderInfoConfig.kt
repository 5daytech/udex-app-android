package com.fridaytech.dex.presentation.orders.model

import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder

data class OrderInfoConfig(
    val order: SignedOrder,
    val info: OrderInfo,
    val side: EOrderSide
)
