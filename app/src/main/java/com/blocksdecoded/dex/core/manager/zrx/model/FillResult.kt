package com.blocksdecoded.dex.core.manager.zrx.model

import java.math.BigDecimal

data class FillResult(
    val receiveAmount: BigDecimal,
    val sendAmount: BigDecimal
)