package com.blocksdecoded.dex.presentation.exchange.view

import java.math.BigDecimal

interface IExchangeViewState {
    var sendAmount: BigDecimal
    var sendCoin: ExchangePairItem?
    var receiveCoin: ExchangePairItem?
}

data class MarketOrderViewState(
    override var sendAmount: BigDecimal,
    var receiveAmount: BigDecimal,
    override var sendCoin: ExchangePairItem?,
    override var receiveCoin: ExchangePairItem?
) : IExchangeViewState

data class LimitOrderViewState(
    override var sendAmount: BigDecimal,
    override var sendCoin: ExchangePairItem?,
    override var receiveCoin: ExchangePairItem?
) : IExchangeViewState

data class ExchangePriceInfo(
    var sendPrice: BigDecimal
)

data class ExchangeReceiveInfo (
    var receiveAmount: BigDecimal
)

data class ExchangePairsInfo (
    val coins: List<ExchangePairItem>,
    val selectedCoin: ExchangePairItem? = null
)
