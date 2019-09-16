package com.blocksdecoded.dex.presentation.convert.model

import java.math.BigDecimal

data class ConvertInfo(
    var fiatAmount: BigDecimal,
    var error: Int
)