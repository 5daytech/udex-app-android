package com.blocksdecoded.dex.presentation.send.model

import java.math.BigDecimal

data class SendInfo(
    var fiatAmount: BigDecimal,
    var error: Boolean
)