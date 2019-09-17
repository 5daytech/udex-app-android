package com.blocksdecoded.dex.presentation.model

import java.math.BigDecimal

data class FeeInfo(
    val coinCode: String,
    val amount: BigDecimal,
    val fiatAmount: BigDecimal,
    val error: Int
)