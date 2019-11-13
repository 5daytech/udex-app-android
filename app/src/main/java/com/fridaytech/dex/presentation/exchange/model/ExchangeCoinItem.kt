package com.fridaytech.dex.presentation.exchange.model

import java.math.BigDecimal

data class ExchangeCoinItem(
    val code: String,
    val name: String,
    val price: BigDecimal,
    val balance: BigDecimal
)
