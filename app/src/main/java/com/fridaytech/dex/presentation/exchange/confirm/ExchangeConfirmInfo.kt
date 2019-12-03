package com.fridaytech.dex.presentation.exchange.confirm

import java.math.BigDecimal

data class ExchangeConfirmInfo(
    val sendCoin: String,
    val receiveCoin: String,
    val sendAmount: BigDecimal,
    val receiveAmount: BigDecimal,
    val showLifeTimeInfo: Boolean,
    val onConfirm: () -> Unit
)
