package com.blocksdecoded.dex.presentation.exchange.view.market

import android.util.Log
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.zrx.model.FillOrderData
import com.blocksdecoded.dex.core.manager.zrx.model.FillResult
import com.blocksdecoded.dex.presentation.exchange.model.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeCoinItem
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeReceiveInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.MarketOrderViewState
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class MarketOrderViewModel: BaseExchangeViewModel<MarketOrderViewState>() {

    override var state: MarketOrderViewState =
        MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            null
        )

    init {
        init()
    }

    private var estimatedSendAmount = BigDecimal.ZERO

    //region Private

    override fun initState(sendItem: ExchangeCoinItem?, receiveItem: ExchangeCoinItem?) {
        state = MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            sendItem,
            receiveItem
        )
        viewState.postValue(state)
    }

    override fun updateReceiveAmount() {
        state.sendAmount.let { amount ->
            val currentMarket = currentMarketPosition
            if (currentMarket < 0) return

            val fillResult = relayer?.calculateFillAmount(
                marketCodes[currentMarket],
                orderSide,
                amount
            ) ?: FillResult(BigDecimal.ZERO, BigDecimal.ZERO)

            val price = relayer?.calculateBasePrice(
                marketCodes[currentMarket],
                orderSide
            ) ?: BigDecimal.ZERO

            exchangePrice.value = price

            state.receiveAmount = fillResult.receiveAmount
            receiveInfo.value = ExchangeReceiveInfo(fillResult.receiveAmount)

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO

            if (fillResult.sendAmount != amount) {
                estimatedSendAmount = fillResult.sendAmount
                Log.d("ololo", "Send amount is $amount")
            } else {
                estimatedSendAmount = amount
            }
        }
    }
    
    private fun marketBuy() {
        state.sendAmount.let { amount ->
            val receiveAmount = state.receiveAmount
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                messageEvent.postValue(R.string.message_wait_blockchain)
                showProcessingEvent.call()

                val fillData = FillOrderData(
                    marketCodes[currentMarketPosition],
                    orderSide,
                    state.receiveAmount
                )

                relayer?.fill(fillData)
                    ?.uiSubscribe(disposables, {
                        processingDismissEvent.call()
                        initState(state.sendCoin, state.receiveCoin)
                        successEvent.postValue(it)
                    }, {
                        Logger.e(it)
                        processingDismissEvent.call()
                        errorEvent.postValue(R.string.error_exchange_failed)
                    })
            } else {
                errorEvent.postValue(R.string.message_invalid_amount)
            }
        }
    }

    private fun confirmExchange() {
        val pair = marketCodes[currentMarketPosition]

        val confirmInfo = ExchangeConfirmInfo(
            if (exchangeSide == ExchangeSide.BID) pair.first else pair.second,
            if (exchangeSide == ExchangeSide.BID) pair.second else pair.first,
            estimatedSendAmount,
            state.receiveAmount
        ) { marketBuy() }

        confirmEvent.postValue(confirmInfo)
    }

    //endregion

    //region Public

    fun requestFillOrder(coins: Pair<String, String>, amount: BigDecimal, orderSide: EOrderSide) {
        focusExchangeEvent.call()

        val baseCoin = exchangeableCoins.firstOrNull { it.code == coins.first } ?: return
        val quoteCoin = exchangeableCoins.firstOrNull { it.code == coins.second } ?: return

        when(orderSide) {
            BUY -> {
                exchangeSide = ExchangeSide.BID
                state.sendCoin = getExchangeItem(baseCoin)
                state.receiveCoin = getExchangeItem(quoteCoin)
                state.sendAmount = amount
                refreshPairs(state)
                viewState.value = state
                updateReceiveAmount()
            }
            SELL -> {
                exchangeSide = ExchangeSide.ASK
                state.sendCoin = getExchangeItem(quoteCoin)
                state.receiveCoin = getExchangeItem(baseCoin)
                state.sendAmount = amount
                refreshPairs(state)
                viewState.value = state
                updateReceiveAmount()
            }
            MY -> {}
        }
    }

    fun onExchangeClick() {
        state.sendAmount.let { amount ->
            val receiveAmount = state.receiveAmount
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                confirmExchange()
            } else {
                errorEvent.postValue(R.string.message_invalid_amount)
            }
        }
    }

    fun onSwitchClick() {
        exchangeSide = when(exchangeSide) {
            ExchangeSide.BID -> ExchangeSide.ASK
            ExchangeSide.ASK -> ExchangeSide.BID
        }

        val sendAmount = state.receiveAmount
        state = MarketOrderViewState(
            sendAmount = BigDecimal.ZERO,
            receiveAmount = BigDecimal.ZERO,
            sendCoin = state.receiveCoin,
            receiveCoin = state.sendCoin
        )

        refreshPairs(state)

        onSendAmountChange(sendAmount)

        viewState.value = state
    }

    //endregion
}