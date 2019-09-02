package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.App.Companion.coinManager
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.zrx.IRelayerAdapter
import com.blocksdecoded.dex.core.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.presentation.orders.model.FillOrderInfo
import com.blocksdecoded.dex.presentation.orders.model.OrderInfoConfig
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.isValidIndex

class OrdersViewModel : CoreViewModel() {
    private val coinManager = App.coinManager
    private val relayerManager = App.relayerAdapterManager
    private val ratesConverter = App.ratesConverter

    private val relayer: IRelayerAdapter?
        get() = relayerManager.mainRelayer
    private var zrxOrdersWatcher: OrdersWatcher? = null

    private val currentPair: Pair<String, String>?
        get() = availablePairs.value?.let { pairs ->
            selectedPairPosition.value?.let {  position ->
                if (pairs.isValidIndex(position)) pairs[position] else null
            }
        }

    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()
    val exchangeCoinSymbol = MutableLiveData<String>()

    val orderInfoEvent = MutableLiveData<OrderInfoConfig>()
    val fillOrderEvent = MutableLiveData<FillOrderInfo>()

    init {
        relayerManager.mainRelayerUpdatedSignal
            .subscribe {
                relayer?.let {
                    zrxOrdersWatcher = OrdersWatcher(coinManager, it, ratesConverter)
                    onRelayerInitialized()
                }
            }.let { disposables.add(it) }
    }

    private fun onRelayerInitialized() {
        zrxOrdersWatcher?.availablePairsSubject?.subscribe({ pairs ->
            availablePairs.postValue(pairs)

            (selectedPairPosition.value ?: 0).let {
                exchangeCoinSymbol.postValue(pairs[it].first)
            }
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.buyOrdersSubject?.subscribe({ orders ->
            buyOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.sellOrdersSubject?.subscribe({ orders ->
            sellOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.myOrdersSubject?.subscribe({orders ->
            myOrders.postValue(orders)
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher?.selectedPairSubject?.subscribe({ position ->
            selectedPairPosition.postValue(position)

            availablePairs.value?.let {
                exchangeCoinSymbol.postValue(it[position].first)
            }
        }, { Logger.e(it) })?.let { disposables.add(it) }

        selectedPairPosition.postValue(0)
        refreshOrders()
    }
    
    private fun refreshOrders() {
        buyOrders.postValue(zrxOrdersWatcher?.uiBuyOrders)
        sellOrders.postValue(zrxOrdersWatcher?.uiSellOrders)
        myOrders.postValue(zrxOrdersWatcher?.uiMyOrders)
    }
    
    fun onPickPair(position: Int) {
        zrxOrdersWatcher?.currentSelectedPair = position
    }

    override fun onCleared() {
        zrxOrdersWatcher?.stop()
        super.onCleared()
    }
    
    fun onOrderClick(position: Int, side: EOrderSide) {
        when(side) {
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
}
