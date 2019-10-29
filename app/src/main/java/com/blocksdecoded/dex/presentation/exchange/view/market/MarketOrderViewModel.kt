package com.blocksdecoded.dex.presentation.exchange.view.market

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.data.manager.zrx.model.FillOrderData
import com.blocksdecoded.dex.data.manager.zrx.model.FillResult
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.model.ExchangeAmountInfo
import com.blocksdecoded.dex.presentation.exchange.model.ExchangeCoinItem
import com.blocksdecoded.dex.presentation.exchange.model.MarketOrderViewState
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.rx.uiSubscribe
import java.math.BigDecimal

class MarketOrderViewModel : BaseExchangeViewModel<MarketOrderViewState>() {

    override var state: MarketOrderViewState =
        MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            null
        )

    private var estimatedSendAmount = BigDecimal.ZERO
    private var estimatedReceiveAmount = BigDecimal.ZERO

    val sendAmount = MutableLiveData<ExchangeAmountInfo>()
    val receiveHintInfo = MutableLiveData<AmountInfo>()

    init {
        init()
    }

    //region Private

    override fun initState(sendItem: ExchangeCoinItem?, receiveItem: ExchangeCoinItem?) {
        state = MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            sendItem,
            receiveItem
        )

        viewState.postValue(state)
        sendHintInfo.postValue(AmountInfo(BigDecimal.ZERO))
        receiveHintInfo.postValue(AmountInfo())
        sendAmount.postValue(ExchangeAmountInfo(BigDecimal.ZERO))
        receiveAmount.postValue(ExchangeAmountInfo(BigDecimal.ZERO))
    }

    override fun updateReceiveAmount() {
        state.sendAmount.let { amount ->
            val currentMarket = currentMarketPosition
            if (currentMarket < 0) return

            val fillResult = relayer?.calculateFillAmount(
                marketCodes[currentMarket],
                orderSide,
                amount
            ) ?: FillResult.empty()

            state.receiveAmount = fillResult.receiveAmount
            estimatedReceiveAmount = fillResult.receiveAmount

            if (fillResult.receiveAmount != receiveAmount.value?.amount) {
                receiveAmount.value = ExchangeAmountInfo(fillResult.receiveAmount)
            }

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO
            estimatedSendAmount = if (fillResult.sendAmount != amount) {
                fillResult.sendAmount
            } else {
                amount
            }

            updateReceiveHint()
        }
    }

    private fun updateReceiveHint() {
        val receiveAmount = receiveAmount.value?.amount ?: BigDecimal.ZERO
        val info = AmountInfo(
            ratesConverter.getCoinsPrice(state.receiveCoin?.code ?: "", receiveAmount),
            0
        )

        receiveHintInfo.postValue(info)
    }

    private fun updateSendAmount() {
        state.receiveAmount.let { amount ->
            val currentMarket = currentMarketPosition
            if (currentMarket < 0) return

            val fillResult = relayer?.calculateSendAmount(
                marketCodes[currentMarket],
                orderSide,
                amount
            ) ?: FillResult.empty()

            state.receiveAmount = amount
            state.sendAmount = fillResult.sendAmount
            estimatedSendAmount = fillResult.sendAmount

            if (fillResult.sendAmount != sendAmount.value?.amount) {
                sendAmount.value = ExchangeAmountInfo(fillResult.sendAmount)
            }

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO && state.sendAmount > BigDecimal.ZERO

            estimatedReceiveAmount = if (fillResult.receiveAmount != amount) {
                fillResult.receiveAmount
            } else {
                amount
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
        val confirmInfo = ExchangeConfirmInfo(
            state.sendCoin?.code ?: "",
            state.receiveCoin?.code ?: "",
            estimatedSendAmount,
            estimatedReceiveAmount
        ) { marketBuy() }

        confirmEvent.postValue(confirmInfo)
    }

    //endregion

    //region Public

    fun requestFillOrder(coins: Pair<String, String>, amount: BigDecimal, orderSide: EOrderSide) {
        focusExchangeEvent.call()

        val baseCoin = exchangeableCoins.firstOrNull { it.code == coins.first } ?: return
        val quoteCoin = exchangeableCoins.firstOrNull { it.code == coins.second } ?: return

        when (orderSide) {
            BUY -> {
                state.sendCoin = getExchangeItem(baseCoin)
                state.receiveCoin = getExchangeItem(quoteCoin)
                state.sendAmount = amount
                refreshPairs(state)
                viewState.value = state
                updateReceiveAmount()
            }
            SELL -> {
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

    fun onReceiveAmountChange(amount: BigDecimal) {
        if (state.receiveAmount.stripTrailingZeros() != amount.stripTrailingZeros()) {
            state.receiveAmount = amount

            updateSendAmount()
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
