package com.blocksdecoded.dex.presentation.models

import java.math.BigDecimal

data class FeeInfo(
    val coinCode: String,
    val amount: BigDecimal,
    val fiatAmount: BigDecimal,
    val error: Int
)