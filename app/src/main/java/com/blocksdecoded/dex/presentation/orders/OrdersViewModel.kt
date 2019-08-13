package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.Logger

class OrdersViewModel : CoreViewModel() {
    private val zrxOrdersWatcher = OrdersWatcher(App.relayerAdapterManager.getMainAdapter())

    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()

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
}
