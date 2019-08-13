package com.blocksdecoded.dex.presentation.exchange.view.limit

import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import java.math.BigDecimal

data class LimitOrderViewState(
    var sendAmount: BigDecimal,
    var sendPrice: BigDecimal,
    var receiveAmount: BigDecimal,
    var sendPair: ExchangePairItem?,
    var receivePair: ExchangePairItem?,
    var sendError: Int = 0,
    var receiveError: Int = 0
)