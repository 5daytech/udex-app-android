package com.blocksdecoded.dex.presentation.model

import java.math.BigDecimal

data class FeeInfo(
    val feeAmount: BigDecimal,
    val feeFiatAmount: BigDecimal,
    val feeError: Int
)