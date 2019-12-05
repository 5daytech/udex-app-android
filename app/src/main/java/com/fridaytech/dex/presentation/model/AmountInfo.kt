package com.fridaytech.dex.presentation.model

import java.math.BigDecimal

data class AmountInfo(
    var value: BigDecimal = BigDecimal.ZERO,
    var error: Int = 0
)
