package com.blocksdecoded.dex.presentation.dialogs.send

import java.math.BigDecimal

data class SendInfo(
    var fiatAmount: BigDecimal,
    var error: Boolean
)