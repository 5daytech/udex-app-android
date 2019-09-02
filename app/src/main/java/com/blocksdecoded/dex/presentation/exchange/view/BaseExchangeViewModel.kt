package com.blocksdecoded.dex.presentation.exchange.view

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.core.zrx.IRelayerAdapter
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

abstract class BaseExchangeViewModel<T: IExchangeViewState> : CoreViewModel() {
    private val relayerManager = App.relayerAdapterManager
    private val coinManager = App.coinManager
    protected val relayer: IRelayerAdapter?
        get() = relayerManager.mainRelayer

    private val adapterManager = App.adapterManager

    protected abstract var state: T
    protected val mReceiveInfo = ExchangeReceiveInfo(BigDecimal.ZERO)

    protected var exchangeSide = ExchangeSide.BID
    protected val orderSide: EOrderSide
        get() = if (exchangeSide == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL

    protected var exchangeableCoins: List<Coin> = listOf()
    protected var marketCodes: List<Pair<String, String>> = listOf()
    protected val currentMarketPosition: Int
        get() {
            val sendCoin = viewState.value?.sendCoin?.code ?: ""
            val receiveCoin = viewState.value?.receiveCoin?.code ?: ""

            return marketCodes.indexOfFirst {
                (it.first == sendCoin && it.second == receiveCoin) ||
                        (it.second == sendCoin && it.first == receiveCoin)
            }
        }

    private var mSendCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            sendCoins.postValue(ExchangePairsInfo(value, state.sendCoin))
        }

    private var mReceiveCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            receiveCoins.postValue(ExchangePairsInfo(value, state.receiveCoin))
        }

    val sendCoins = MutableLiveData<ExchangePairsInfo>()
    val receiveCoins = MutableLiveData<ExchangePairsInfo>()
    val viewState = MutableLiveData<T>()
    val receiveInfo = MutableLiveData<ExchangeReceiveInfo>()

    val exchangeEnabled = MutableLiveData<Boolean>()
    val exchangePrice = MutableLiveData<BigDecimal>()

    val successEvent = SingleLiveEvent<String>()
    val confirmEvent = SingleLiveEvent<ExchangeConfirmInfo>()
    val showProcessingEvent = SingleLiveEvent<Unit>()
    val processingDismissEvent = SingleLiveEvent<Unit>()
    val focusExchangeEvent = SingleLiveEvent<Unit>()

    protected fun init() {
        exchangeEnabled.value = false

        relayerManager.mainRelayerUpdatedSignal
            .subscribe {
                relayer?.availablePairsSubject
                    ?.subscribe {
                        marketCodes = it

                        exchangeableCoins = coinManager.coins.filter { coin ->
                            marketCodes.firstOrNull { pair ->
                                pair.first.equals(coin.code, true) ||
                                        pair.second.equals(coin.code, true)
                            } != null
                        }

                        refreshPairs(null)

                        initState(mSendCoins.first(), mReceiveCoins.first())

                        adapterManager.adaptersUpdatedSignal
                            .subscribe {
                                exchangeableCoins.forEach {  coin ->
                                    adapterManager.adapters
                                        .firstOrNull { it.coin.code == coin.code }
                                        ?.balanceUpdatedFlowable
                                        ?.uiSubscribe(disposables, {
                                            refreshPairs(viewState.value)
                                        })
                                }
                            }.let { disposables.add(it) }
                    }?.let { disposables.add(it) }
            }.let { disposables.add(it) }
    }

    protected abstract fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?)
    protected abstract fun updateReceiveAmount()

    fun onSendAmountChange(amount: BigDecimal) {
        if (state.sendAmount != amount) {
            state.sendAmount = amount

            updateReceiveAmount()
        }
    }

    fun onMaxClick() {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == state.sendCoin?.code }
        if (adapter != null) {
            val amount = adapter.availableBalance(null, FeeRatePriority.HIGH)
            onSendAmountChange(amount)
            viewState.value = state
        }
    }

    open fun onReceiveCoinPick(position: Int) {
        if (state.receiveCoin?.code != mReceiveCoins[position].code) {
            state.receiveCoin = mReceiveCoins[position]
            updateReceiveAmount()
        }
    }

    open fun onSendCoinPick(position: Int) {
        val pair = mSendCoins[position]

        if (pair.code == state.receiveCoin?.code) {
            state.sendCoin = pair
            state.receiveCoin = null

            refreshPairs(state, false)
            viewState.value = state
        } else if (state.sendCoin?.code != pair.code) {
            state.sendCoin = mSendCoins[position]

            refreshPairs(state, false)
            updateReceiveAmount()
        }
    }

    //region Market pairs refresh

    protected open fun refreshPairs(state: T?, refreshSendCoins: Boolean = true)  {
        if (refreshSendCoins) {
            mSendCoins = getAvailableSendCoins()
        }

        val sendCoin = state?.sendCoin?.code ?: mSendCoins.first().code
        mReceiveCoins = getAvailableReceiveCoins(sendCoin)
    }

    private fun getAvailableSendCoins(): List<ExchangePairItem> = exchangeableCoins
        .filter { coin -> marketCodes
            .firstOrNull {
                when(this.exchangeSide) {
                    ExchangeSide.BID -> it.first == coin.code
                    ExchangeSide.ASK -> it.second == coin.code
                }
            } != null
        }
        .map { getExchangeItem(it) }

    private fun getAvailableReceiveCoins(baseCoinCode: String): List<ExchangePairItem> =
        exchangeableCoins
            .filter { coin ->
                marketCodes.firstOrNull {
                    when(this.exchangeSide) {
                        ExchangeSide.BID -> it.first.equals(baseCoinCode, true) &&
                                it.second.equals(coin.code, true)

                        ExchangeSide.ASK -> it.second.equals(baseCoinCode, true) &&
                                it.first.equals(coin.code, true)
                    }
                } != null
            }
            .map { getExchangeItem(it) }

    protected fun getExchangeItem(coin: Coin): ExchangePairItem {
        val balance = adapterManager.adapters
            .firstOrNull { it.coin.code == coin.code }?.balance ?: BigDecimal.ZERO

        return ExchangePairItem(coin.code, coin.title, BigDecimal.ZERO, balance)
    }

    //endregion
}