package com.fridaytech.dex.presentation.orders.model

import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.relayer.model.OrderRecord

data class OrderInfoConfig(
    val orderRecord: OrderRecord,
    val info: OrderInfo,
    val side: EOrderSide
)
