package com.blocksdecoded.dex.presentation.dialogs.send

import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import java.math.BigDecimal

class SendUserInput {
    var amount: BigDecimal = BigDecimal.ZERO
    var address: String? = null
    var feePriority: FeeRatePriority = FeeRatePriority.MEDIUM
}