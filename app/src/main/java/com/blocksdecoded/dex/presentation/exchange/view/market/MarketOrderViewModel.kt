package com.blocksdecoded.dex.presentation.exchange.view.market

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.subscribeUi
import java.math.BigDecimal

class MarketOrderViewModel: CoreViewModel() {
    private val relayer = App.relayerAdapterManager.getMainAdapter()
    private val adapterManager = App.adapterManager

    private var exchangeableCoins: List<Coin> = listOf()
    private var coinPairsCodes: List<Pair<String, String>> = listOf()
    private val currentPairPosition: Int
        get() {
            val sendCoin = viewState.value?.sendPair?.code ?: ""
            val receiveCoin = viewState.value?.receivePair?.code ?: ""

            return coinPairsCodes.indexOfFirst {
                (it.first == sendCoin && it.second == receiveCoin) ||
                        (it.second == sendCoin && it.first == receiveCoin)
            }
        }

    private var exchangeState = ExchangeSide.BID

    private var mSendCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            sendCoins.value = value
        }

    private var mReceiveCoins: List<ExchangePairItem> = listOf()
        set(value) {
            field = value
            receiveCoins.value = value
        }
    
    val sendCoins = MutableLiveData<List<ExchangePairItem>>()
    val receiveCoins = MutableLiveData<List<ExchangePairItem>>()
    val viewState = MutableLiveData<MarketOrderViewState>()
    val exchangeEnabled = MutableLiveData<Boolean>()
    val exchangePrice = MutableLiveData<BigDecimal>()

    val successEvent = SingleLiveEvent<String>()
    val confirmEvent = SingleLiveEvent<ExchangeConfirmInfo>()

    init {
        exchangeEnabled.value = false
        relayer.availablePairsSubject
            .subscribe {
                coinPairsCodes = it

                exchangeableCoins = CoinManager.coins.filter { coin ->
                    coinPairsCodes.firstOrNull { pair ->
                        pair.first.equals(coin.code, true) ||
                                pair.second.equals(coin.code, true)
                    } != null
                }

                refreshPairs(null)

                initState(mSendCoins.first(), mReceiveCoins.first())
            }.let { disposables.add(it) }
    }

    //region Private

    private fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?) {
        viewState.value = MarketOrderViewState(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            sendItem,
            receiveItem
        )
    }

    private fun getAvailableSendCoins(): List<ExchangePairItem> {
        // Send only available pair exchangeableCoins
        return exchangeableCoins
            .filter { coin -> coinPairsCodes
                .firstOrNull {
                    when(this.exchangeState) {
                        ExchangeSide.BID -> it.first == coin.code
                        ExchangeSide.ASK -> it.second == coin.code
                    }
                } != null
            }
            .map { ExchangePairItem(it.code, it.title, 0.toBigDecimal(), 0.toBigDecimal()) }
    }

    private fun getAvailableReceiveCoins(baseCoinCode: String): List<ExchangePairItem> {
        // Receive available send coin pairs
        return exchangeableCoins
            .filter { coin ->
                coinPairsCodes.firstOrNull {
                    when(this.exchangeState) {
                        ExchangeSide.BID -> it.first.equals(baseCoinCode, true) &&
                                it.second.equals(coin.code, true)

                        ExchangeSide.ASK -> it.second.equals(baseCoinCode, true) &&
                                it.first.equals(coin.code, true)
                    }
                } != null
            }
            .map { ExchangePairItem(it.code, it.title, 0.toBigDecimal(), 0.toBigDecimal()) }
    }

    private fun refreshPairs(state: MarketOrderViewState?, refreshSendCoins: Boolean = true) {
        if (refreshSendCoins) {
            mSendCoins = getAvailableSendCoins()
        }

        val sendCoin = state?.sendPair?.code ?: mSendCoins.first().code
        mReceiveCoins = getAvailableReceiveCoins(sendCoin)
    }

    private fun updateReceivePrice() {
        viewState.value?.sendAmount?.let { amount ->
            val receiveAmount = relayer.calculateFillAmount(
                coinPairsCodes[currentPairPosition],
                if (exchangeState == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL,
                amount
            )

            exchangePrice.value = relayer.calculateBasePrice(
                coinPairsCodes[currentPairPosition],
                if (exchangeState == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL
            )

            viewState.value?.receiveAmount = receiveAmount

            viewState.value = viewState.value

            exchangeEnabled.value = receiveAmount > BigDecimal.ZERO
        }
    }
    
    private fun marketBuy() {
        viewState.value?.sendAmount?.let { amount ->
            val receiveAmount = viewState.value?.receiveAmount ?: BigDecimal.ZERO
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                messageEvent.postValue(R.string.message_wait_blockchain)
            
                relayer.fill(
                    coinPairsCodes[currentPairPosition],
                    if (exchangeState == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL,
                    if (exchangeState == ExchangeSide.BID) amount else viewState.value?.receiveAmount ?: BigDecimal.ZERO
                ).subscribeUi(disposables, {
                    initState(viewState.value?.sendPair, viewState.value?.receivePair)
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
        val pair = coinPairsCodes[currentPairPosition]

        val confirmInfo = ExchangeConfirmInfo(
            pair.first,
            pair.second,
            viewState.value?.sendAmount ?: BigDecimal.ZERO,
            viewState.value?.receiveAmount ?: BigDecimal.ZERO
        ) { marketBuy() }

        confirmEvent.value = confirmInfo
    }

    //endregion

    //region Public

    fun onReceiveCoinPick(position: Int) {
        viewState.value?.receivePair = mReceiveCoins[position]
        updateReceivePrice()
    }

    fun onSendCoinPick(position: Int) {
        viewState.value?.sendPair = mSendCoins[position]
        refreshPairs(viewState.value, false)
        updateReceivePrice()
    }

    fun onSendAmountChange(amount: BigDecimal) {
        if (viewState.value?.sendAmount != amount) {
            viewState.value?.sendAmount = amount

            updateReceivePrice()
        }
    }

    fun onMaxClick() {
        val adapter = adapterManager.adapters.firstOrNull { it.coin.code == viewState.value?.sendPair?.code }
        if (adapter != null) {

        }
    }

    fun onExchangeClick() {
        viewState.value?.sendAmount?.let { amount ->
            val receiveAmount = viewState.value?.receiveAmount ?: BigDecimal.ZERO
            if (amount > BigDecimal.ZERO && receiveAmount > BigDecimal.ZERO) {
                showConfirm()
            } else {
                errorEvent.postValue(R.string.message_invalid_amount)
            }
        }
    }

    fun onSwitchClick() {
        exchangeState = when(exchangeState) {
            ExchangeSide.BID -> ExchangeSide.ASK
            ExchangeSide.ASK -> ExchangeSide.BID
        }

        val newState = MarketOrderViewState(
            sendAmount = viewState.value?.receiveAmount ?: BigDecimal.ZERO,
            receiveAmount = viewState.value?.sendAmount ?: BigDecimal.ZERO,
            sendPair = viewState.value?.receivePair!!,
            receivePair = viewState.value?.sendPair!!
        )

        refreshPairs(newState)

        viewState.value = newState
    }

    //endregion
}