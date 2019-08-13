package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.OrderInfo
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.zrxkit.model.SignedOrder

class OrdersViewModel : CoreViewModel() {
    private val adapter = App.relayerAdapterManager.getMainAdapter()
    private val zrxOrdersWatcher = OrdersWatcher(adapter)

    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()

    val orderInfoEvent = MutableLiveData<OrderInfo>()

    init {
        zrxOrdersWatcher.availablePairsSubject.subscribe({ pairs ->
            availablePairs.value = pairs
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher.buyOrdersSubject.subscribe({ orders ->
            buyOrders.value = orders
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher.sellOrdersSubject.subscribe({ orders ->
            sellOrders.value = orders
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher.myOrdersSubject.subscribe({orders ->
            myOrders.value = orders
        }, { Logger.e(it) })?.let { disposables.add(it) }

        zrxOrdersWatcher.selectedPairSubject.subscribe({ position ->
            selectedPairPosition.value = position
        }, { Logger.e(it) })?.let { disposables.add(it) }
        
        refreshOrders()
    }
    
    private fun refreshOrders() {
        buyOrders.value = zrxOrdersWatcher.uiBuyOrders
        sellOrders.value = zrxOrdersWatcher.uiSellOrders
        myOrders.value = zrxOrdersWatcher.uiMyOrders
    }
    
    fun onPickPair(position: Int) {
        zrxOrdersWatcher.currentSelectedPair = position
    }

    override fun onCleared() {
        zrxOrdersWatcher.stop()
        super.onCleared()
    }
    
    fun onOrderClick(position: Int, side: EOrderSide) {
        if (myOrders.value != null && side == EOrderSide.MY
            && myOrders.value!!.isValidIndex(position)) {
            val order = zrxOrdersWatcher.getMyOrder(position, side)
            
            if (order != null) {
                orderInfoEvent.postValue(OrderInfo(order.first, order.second))
            }
        }
    }
}
