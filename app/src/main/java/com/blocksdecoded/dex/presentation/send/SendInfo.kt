package com.blocksdecoded.dex.presentation.send

import java.math.BigDecimal

data class SendInfo(
    var fiatAmount: BigDecimal,
    var error: Boolean
)