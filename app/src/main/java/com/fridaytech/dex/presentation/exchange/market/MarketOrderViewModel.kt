package com.fridaytech.dex.presentation.exchange.market

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.R
import com.fridaytech.dex.data.zrx.model.FillOrderData
import com.fridaytech.dex.data.zrx.model.FillResult
import com.fridaytech.dex.presentation.exchange.BaseExchangeViewModel
import com.fridaytech.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.fridaytech.dex.presentation.exchange.model.ExchangeAmountInfo
import com.fridaytech.dex.presentation.exchange.model.ExchangeCoinItem
import com.fridaytech.dex.presentation.exchange.model.MarketOrderViewState
import com.fridaytech.dex.presentation.model.AmountInfo
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.presentation.orders.model.EOrderSide.*
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.rx.uiSubscribe
import java.math.BigDecimal
import java.math.RoundingMode

// TODO: This class needs refactoring
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

    private fun BigDecimal.scaleToView(): BigDecimal = setScale(
        9, RoundingMode.FLOOR
    ).stripTrailingZeros()

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
        estimatedReceiveAmount = BigDecimal.ZERO
        estimatedSendAmount = BigDecimal.ZERO
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
            estimatedReceiveAmount = fillResult.receiveAmount

            val roundedReceiveAmount = fillResult.receiveAmount.scaleToView()

            state.receiveAmount = roundedReceiveAmount
            if (roundedReceiveAmount != receiveAmount.value?.amount) {
                receiveAmount.value = ExchangeAmountInfo(roundedReceiveAmount)
            }

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO
            estimatedSendAmount = if (fillResult.sendAmount != amount) {
                fillResult.sendAmount
            } else {
                amount
            }

            updateSendHint(amount)
            updateReceiveHint()
        }
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

            estimatedSendAmount = fillResult.sendAmount

            val roundedSendAmount = fillResult.sendAmount.scaleToView()

            state.receiveAmount = amount
            state.sendAmount = roundedSendAmount

            if (roundedSendAmount != sendAmount.value?.amount) {
                sendAmount.value = ExchangeAmountInfo(roundedSendAmount)
            }

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO && state.sendAmount > BigDecimal.ZERO

            estimatedReceiveAmount = if (fillResult.receiveAmount != amount) {
                fillResult.receiveAmount
            } else {
                amount
            }

            updateSendHint(roundedSendAmount)
            updateReceiveHint()
        }
    }

    private fun updateReceiveHint() {
        val receiveAmount = state.receiveAmount
        val info = AmountInfo(
            ratesConverter.getCoinsPrice(state.receiveCoin?.code ?: "", receiveAmount),
            0
        )

        receiveHintInfo.postValue(info)
    }

    private fun marketBuy() {
        state.sendAmount.let { amount ->
            if (amount > BigDecimal.ZERO && estimatedReceiveAmount > BigDecimal.ZERO) {
                messageEvent.postValue(R.string.message_wait_blockchain)
                showProcessingEvent.call()

                val fillData = FillOrderData(
                    marketCodes[currentMarketPosition],
                    orderSide,
                    estimatedReceiveAmount
                )

                relayer?.fill(fillData)
                    ?.uiSubscribe(disposables, {
                        processingDismissEvent.call()
                        initState(state.sendCoin, state.receiveCoin)
                        transactionsSentEvent.postValue(it)
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
            estimatedReceiveAmount,
            showLifeTimeInfo = false
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
                onSendAmountChange(amount)
                refreshPairs(state)
                viewState.value = state
            }
            SELL -> {
                state.sendCoin = getExchangeItem(quoteCoin)
                state.receiveCoin = getExchangeItem(baseCoin)
                onSendAmountChange(amount)
                refreshPairs(state)
                viewState.value = state
            }
            MY -> {}
        }
    }

    fun onReceiveAmountChange(amount: BigDecimal) {
        if (state.receiveAmount.stripTrailingZeros() != amount) {
            state.receiveAmount = amount

            updateSendAmount()
        }
    }

    override fun onSendAmountChange(amount: BigDecimal) {
        super.onSendAmountChange(amount.scaleToView())
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
