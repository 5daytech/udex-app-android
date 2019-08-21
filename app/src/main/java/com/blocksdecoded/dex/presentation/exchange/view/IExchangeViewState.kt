package com.blocksdecoded.dex.presentation.exchange.view

import java.math.BigDecimal

data class ExchangeReceiveInfo (
    var receiveAmount: BigDecimal
)

interface IExchangeViewState {
    var sendAmount: BigDecimal
    var sendPair: ExchangePairItem?
    var receivePair: ExchangePairItem?
}