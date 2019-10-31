package com.blocksdecoded.dex.presentation.orders.model

import java.math.BigDecimal

data class CancelOrderInfo(
    val orderCount: Int,
    val estimatedFee: BigDecimal,
    val feeCoinCode: String?,
    val processingDuration: Long,
    val onConfirm: () -> Unit
)
