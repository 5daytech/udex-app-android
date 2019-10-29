package com.blocksdecoded.dex.presentation.exchange.model

import java.math.BigDecimal

interface IExchangeViewState {
    var sendAmount: BigDecimal
    var sendCoin: ExchangeCoinItem?
    var receiveCoin: ExchangeCoinItem?
}

data class MarketOrderViewState(
    override var sendAmount: BigDecimal,
    var receiveAmount: BigDecimal,
    override var sendCoin: ExchangeCoinItem?,
    override var receiveCoin: ExchangeCoinItem?
) : IExchangeViewState

data class LimitOrderViewState(
    override var sendAmount: BigDecimal,
    override var sendCoin: ExchangeCoinItem?,
    override var receiveCoin: ExchangeCoinItem?
) : IExchangeViewState

data class ExchangeAmountInfo(
    var amount: BigDecimal
)

data class ExchangePairsInfo(
    val coins: List<ExchangeCoinItem>,
    val selectedCoin: ExchangeCoinItem? = null
)
