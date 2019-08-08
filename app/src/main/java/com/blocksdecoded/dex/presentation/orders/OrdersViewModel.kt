package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.zrx.OrdersWatcher
import com.blocksdecoded.dex.presentation.orders.model.UiOrder

class OrdersViewModel : CoreViewModel() {
    private val zrxOrdersWatcher = OrdersWatcher(App.relayerAdapterManager.getMainAdapter())

    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()

    init {
        zrxOrdersWatcher.availablePairsSubject.subscribe { pairs ->
            availablePairs.value = pairs
        }?.let { disposables.add(it) }

        zrxOrdersWatcher.buyOrdersSubject.subscribe { orders ->
            buyOrders.value = orders
        }?.let { disposables.add(it) }

        zrxOrdersWatcher.sellOrdersSubject.subscribe { orders ->
            sellOrders.value = orders
        }?.let { disposables.add(it) }

        zrxOrdersWatcher.myOrdersSubject.subscribe { orders ->
            myOrders.value = orders
        }?.let { disposables.add(it) }

        zrxOrdersWatcher.selectedPairSubject.subscribe { position ->
            selectedPairPosition.value = position
        }?.let { disposables.add(it) }
        
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
