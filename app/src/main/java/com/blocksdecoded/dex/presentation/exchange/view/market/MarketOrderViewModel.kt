package com.blocksdecoded.dex.presentation.exchange.view.market

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.exchange.view.ExchangeReceiveInfo
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class MarketOrderViewModel: BaseExchangeViewModel<MarketOrderViewState>() {

    override var state: MarketOrderViewState = MarketOrderViewState(
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        null,
        null
    )

    init {
        init()
    }

    //region Private

    override fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?) {
        state = MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            sendItem,
            receiveItem
        )
        viewState.value = state
    }

    private fun updateReceivePrice() {
        state.sendAmount.let { amount ->
            val receiveAmount = relayer.calculateFillAmount(
                marketCodes[currentMarketPosition],
                orderSide,
                amount
            )

            exchangePrice.value = relayer.calculateBasePrice(
                marketCodes[currentMarketPosition],
                orderSide
            )

            state.receiveAmount = receiveAmount
            receiveInfo.value = ExchangeReceiveInfo(receiveAmount)

            exchangeEnabled.value = state.receiveAmount > BigDecimal.ZERO
        }
    }
    
    private fun marketBuy() {
        state.sendAmount.let { amount ->
            val receiveAmount = state.receiveAmount
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                messageEvent.postValue(R.string.message_wait_blockchain)

                val amount = if (exchangeSide == ExchangeSide.BID) amount else state.receiveAmount
                relayer.fill(
                    marketCodes[currentMarketPosition],
                    orderSide,
                    amount
                ).uiSubscribe(disposables, {
                    initState(state.sendPair, state.receivePair)
                    successEvent.postValue(it)
                }, {
                    errorEvent.postValue(R.string.error_exchange_failed)
                })
            } else {
                errorEvent.postValue(R.string.message_invalid_amount)
            }
        }
    }

    private fun showConfirm() {
        val pair = marketCodes[currentMarketPosition]

        val confirmInfo = ExchangeConfirmInfo(
            if (exchangeSide == ExchangeSide.BID) pair.first else pair.second,
            if (exchangeSide == ExchangeSide.BID) pair.second else pair.first,
            state.sendAmount,
            state.receiveAmount
        ) { marketBuy() }

        confirmEvent.value = confirmInfo
    }

    //endregion

    //region Public

    fun onReceiveCoinPick(position: Int) {
        if (state.receivePair?.code != mReceiveCoins[position].code) {
            state.receivePair = mReceiveCoins[position]
            updateReceivePrice()
        }
    }

    fun onSendCoinPick(position: Int) {
        val pair = mSendCoins[position]
        if (state.sendPair?.code != pair.code) {
            state.sendPair = mSendCoins[position]
            refreshPairs(state, false)
            updateReceivePrice()
        }
    }

    override fun onSendAmountChange(amount: BigDecimal) {
        if (state.sendAmount != amount) {
            state.sendAmount = amount

            updateReceivePrice()
        }
    }

    fun onExchangeClick() {
        state.sendAmount.let { amount ->
            val receiveAmount = state.receiveAmount
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                showConfirm()
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

        state = MarketOrderViewState(
            sendAmount = state.receiveAmount,
            receiveAmount = state.sendAmount,
            sendPair = state.receivePair,
            receivePair = state.sendPair
        )

        refreshPairs(state)

        viewState.value = state
    }

    //endregion
}