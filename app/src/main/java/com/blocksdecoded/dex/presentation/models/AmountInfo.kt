package com.blocksdecoded.dex.presentation.models

import java.math.BigDecimal

data class AmountInfo(
    var value: BigDecimal,
    var error: Int = 0
)
