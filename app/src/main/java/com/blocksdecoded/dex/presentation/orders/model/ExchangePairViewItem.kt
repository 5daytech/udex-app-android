package com.blocksdecoded.dex.presentation.orders.model

import java.math.BigDecimal

data class ExchangePairViewItem(
    val baseCoin: String,
    val basePrice: BigDecimal,
    val quoteCoin: String,
    val quotePrice: BigDecimal
)