package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.manager.zrx.IRelayerAdapter
import com.blocksdecoded.dex.core.manager.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.*
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.isValidIndex
import java.math.BigDecimal

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
            selectedPairPosition.value?.let {  position ->
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

    init {
        relayerManager.mainRelayerUpdatedSignal
            .subscribe {
                relayer?.let {
                    zrxOrdersWatcher = OrdersWatcher(coinManager, it, ratesConverter)
                    onRelayerInitialized()
                }
            }.let { disposables.add(it) }

        ratesManager.marketsUpdateSubject.subscribe {
            availablePairs.value?.let {
                onPairsRefresh(it.map { it.baseCoin to it.quoteCoin })
            }
        }.let { disposables.add(it) }
    }

    private fun onRelayerInitialized() {
        zrxOrdersWatcher?.availablePairsSubject?.subscribe({
            val pairs = relayer?.exchangePairs?.map { it.baseCoinCode to it.quoteCoinCode } ?: listOf()
            onPairsRefresh(pairs)

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
                exchangeCoinSymbol.postValue(it[position].baseCoin)
            }
        }, { Logger.e(it) })?.let { disposables.add(it) }

        selectedPairPosition.postValue(0)
        refreshOrders()
    }

    private fun onPairsRefresh(pairs: List<Pair<String, String>>) {
        val exchangePairs = pairs.map {
            ExchangePairViewItem(
                it.first,
                ratesConverter.getTokenPrice(it.first),
                it.second,
                ratesConverter.getTokenPrice(it.second)
            )
        }

        availablePairs.postValue(exchangePairs)
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
