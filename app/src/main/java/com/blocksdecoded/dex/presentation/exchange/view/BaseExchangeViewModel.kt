package com.blocksdecoded.dex.presentation.exchange.view

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

abstract class BaseExchangeViewModel<T: IExchangeViewState> : CoreViewModel() {
    protected val relayer = App.relayerAdapterManager.getMainAdapter()
    protected val adapterManager = App.adapterManager

    protected abstract var state: T
    protected val mReceiveInfo = ExchangeReceiveInfo(BigDecimal.ZERO)
    protected var exchangeSide = ExchangeSide.BID
    protected var exchangeableCoins: List<Coin> = listOf()
    protected var marketCodes: List<Pair<String, String>> = listOf()
    protected val currentMarketPosition: Int
        get() {
            val sendCoin = viewState.value?.sendPair?.code ?: ""
            val receiveCoin = viewState.value?.receivePair?.code ?: ""

            return marketCodes.indexOfFirst {
                (it.first == sendCoin && it.second == receiveCoin) ||
                        (it.second == sendCoin && it.first == receiveCoin)
            }
        }

    protected var mSendCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            sendCoins.value = value
        }

    protected var mReceiveCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            receiveCoins.value = value
        }

    val sendCoins = MutableLiveData<List<ExchangePairItem>>()
    val receiveCoins = MutableLiveData<List<ExchangePairItem>>()
    val viewState = MutableLiveData<T>()
    val receiveInfo = MutableLiveData<ExchangeReceiveInfo>()

    val exchangeEnabled = MutableLiveData<Boolean>()
    val exchangePrice = MutableLiveData<BigDecimal>()

    val successEvent = SingleLiveEvent<String>()
    val confirmEvent = SingleLiveEvent<ExchangeConfirmInfo>()

    protected fun init() {
        exchangeEnabled.value = false
        relayer.availablePairsSubject
            .subscribe {
                marketCodes = it

                exchangeableCoins = CoinManager.coins.filter { coin ->
                    marketCodes.firstOrNull { pair ->
                        pair.first.equals(coin.code, true) ||
                                pair.second.equals(coin.code, true)
                    } != null
                }

                refreshPairs(null)

                initState(mSendCoins.first(), mReceiveCoins.first())
            }.let { disposables.add(it) }

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
    }

    protected abstract fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?)
    abstract fun onSendAmountChange(amount: BigDecimal)

    fun onMaxClick() {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == state.sendPair?.code }
        if (adapter != null) {
            val amount = adapter.availableBalance(null, FeeRatePriority.HIGH)
            onSendAmountChange(amount)
            viewState.value = state
        }
    }

    //region Market pairs refresh

    protected fun refreshPairs(state: T?, refreshSendCoins: Boolean = true)  {
        if (refreshSendCoins) {
            mSendCoins = getAvailableSendCoins()
        }

        val sendCoin = state?.sendPair?.code ?: mSendCoins.first().code
        mReceiveCoins = getAvailableReceiveCoins(sendCoin)
    }

    private fun getAvailableSendCoins(): List<ExchangePairItem> {
        return exchangeableCoins
            .filter { coin -> marketCodes
                .firstOrNull {
                    when(this.exchangeSide) {
                        ExchangeSide.BID -> it.first == coin.code
                        ExchangeSide.ASK -> it.second == coin.code
                    }
                } != null
            }
            .map { getExchangeItem(it) }
    }

    private fun getAvailableReceiveCoins(baseCoinCode: String): List<ExchangePairItem> {
        return exchangeableCoins
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
    }

    private fun getExchangeItem(coin: Coin): ExchangePairItem {
        val balance = adapterManager.adapters
            .firstOrNull { it.coin.code == coin.code }?.balance ?: BigDecimal.ZERO

        return ExchangePairItem(coin.code, coin.title, BigDecimal.ZERO, balance)
    }

    //endregion
}