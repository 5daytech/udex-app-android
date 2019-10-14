package com.blocksdecoded.dex.presentation.models

import java.math.BigDecimal

data class AmountInfo(
    var fiatAmount: BigDecimal,
    var error: Int
)
