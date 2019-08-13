package com.blocksdecoded.dex.presentation.orders.model

import com.blocksdecoded.zrxkit.model.SignedOrder

data class OrderInfo(
	val order: SignedOrder,
	val side: EOrderSide
)