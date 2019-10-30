package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.data.manager.zrx.IRelayerAdapter
import com.blocksdecoded.dex.data.manager.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.*
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.rx.uiSubscribe

class OrdersViewModel : CoreViewModel() {
    private val coinManager = App.coinManager
    private val relayerManager = App.relayerAdapterManager
    private val ratesConverter = App.ratesConverter
    private val ratesManager = App.ratesManager

    private val relayer: IRelayerAdapter?
        get() = relayerManager.mainRelayer
    private var zrxOrdersWatcher: OrdersWatcher? = null

    private val currentPair: Pair<String, String>?
        get() = availablePairs.value?.let { pairs ->
            selectedPairPosition.value?.let { position ->
                if (pairs.isValidIndex(position))
                    pairs[position].baseCoin to pairs[position].quoteCoin
                else
                    null
            }
        }

    val availablePairs = MutableLiveData<List<ExchangePairViewItem>>()
    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val exchangeCoinSymbol = MutableLiveData<String>()

    val orderInfoEvent = MutableLiveData<OrderInfoConfig>()
    val fillOrderEvent = MutableLiveData<FillOrderInfo>()

    val cancelAllConfirmEvent = SingleLiveEvent<Unit>()

    init {
        relayerManager.mainRelayerUpdatedSignal
            .subscribe {
                relayer?.let {
                    zrxOrdersWatcher = OrdersWatcher(coinManager, it, ratesConverter)
                    onRelayerInitialized()
                }
            }.let { disposables.add(it) }

        ratesManager.ratesUpdateSubject.subscribe {
            onPairsRefresh()
        }.let { disposables.add(it) }
    }

    //region Private

    private fun onRelayerInitialized() {
        relayer?.pairsUpdateSubject?.subscribe({
            onPairsRefresh()
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.buyOrdersSubject?.subscribe({ orders ->
            buyOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.sellOrdersSubject?.subscribe({ orders ->
            sellOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.myOrdersSubject?.subscribe({ orders ->
            myOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.selectedPairSubject?.subscribe({ position ->
            selectedPairPosition.postValue(position)

            availablePairs.value?.let {
                exchangeCoinSymbol.postValue(it[position].baseCoin)
            }
        }, { Logger.e(it) })?.let { disposables.add(it) }

        selectedPairPosition.postValue(0)
        refreshOrders()
    }

    private fun onPairsRefresh() {
        val pairs = relayer?.exchangePairs
            ?.map { it.baseCoinCode to it.quoteCoinCode } ?: listOf()

        val exchangePairs = pairs.map {
            ExchangePairViewItem(
                it.first,
                ratesConverter.getTokenPrice(it.first),
                it.second,
                ratesConverter.getTokenPrice(it.second)
            )
        }

        availablePairs.postValue(exchangePairs)

        if (pairs.isNotEmpty()) {
            val selectedPair = zrxOrdersWatcher?.currentSelectedPair
            selectedPairPosition.postValue(selectedPair)

            (selectedPair ?: 0).let {
                exchangeCoinSymbol.postValue(pairs[it].first)
            }
        }
    }

    private fun refreshOrders() {
        buyOrders.postValue(zrxOrdersWatcher?.uiBuyOrders)
        sellOrders.postValue(zrxOrdersWatcher?.uiSellOrders)
        myOrders.postValue(zrxOrdersWatcher?.uiMyOrders)
    }

    //endregion

    fun onPickPair(position: Int) {
        zrxOrdersWatcher?.currentSelectedPair = position
    }

    fun onOrderClick(position: Int, side: EOrderSide) {
        when (side) {
            BUY -> {
                if (buyOrders.value != null && buyOrders.value!!.isValidIndex(position)) {
                    currentPair?.let {
                        fillOrderEvent.postValue(FillOrderInfo(it, buyOrders.value!![position].takerAmount, side))
                    }
                }
            }
            SELL -> {
                if (sellOrders.value != null && sellOrders.value!!.isValidIndex(position)) {
                    currentPair?.let {
                        fillOrderEvent.postValue(FillOrderInfo(it, sellOrders.value!![position].takerAmount, side))
                    }
                }
            }
            MY -> {
                if (myOrders.value != null && myOrders.value!!.isValidIndex(position)) {
                    val order = zrxOrdersWatcher?.getMyOrder(position, side)

                    if (order != null) {
                        orderInfoEvent.postValue(OrderInfoConfig(
                            order.first,
                            order.second,
                            order.third
                        ))
                    }
                }
            }
        }
    }

    fun onCancelAllClick() {
        if (!myOrders.value.isNullOrEmpty()) {
            val orders = zrxOrdersWatcher?.getMyOrders()

            if (orders != null) {
                cancelAllConfirmEvent.call()
            }
        }
    }

    fun onCancelAllConfirm() {
        if (!myOrders.value.isNullOrEmpty()) {
            val orders = zrxOrdersWatcher?.getMyOrders()

            if (orders != null) {
                relayer?.batchCancelOrders(orders)
                    ?.uiSubscribe(disposables, {
                        messageEvent.postValue(R.string.message_cancel_started)
                    }, {
                        errorEvent.postValue(R.string.error_cancel_order)
                    })
            }
        }
    }

    override fun onCleared() {
        zrxOrdersWatcher?.stop()
        super.onCleared()
    }
}
