package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.presentation.orders.model.UiOrder

class OrdersViewModel : CoreViewModel() {
    private val zrxRelayerAdapter = App.relayerAdapterManager.mainAdapter
    
    val selectedPairPosition = MutableLiveData<Int>()
    val buyOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val sellOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val myOrders: MutableLiveData<List<UiOrder>> = MutableLiveData()
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()

    init {
        zrxRelayerAdapter?.availablePairsSubject?.subscribe { pairs ->
            availablePairs.value = pairs
        }?.let { disposables.add(it) }
    
        zrxRelayerAdapter?.buyOrdersSubject?.subscribe { orders ->
            buyOrders.value = orders
        }?.let { disposables.add(it) }
    
        zrxRelayerAdapter?.sellOrdersSubject?.subscribe { orders ->
            sellOrders.value = orders
        }?.let { disposables.add(it) }
    
        zrxRelayerAdapter?.myOrdersSubject?.subscribe { orders ->
            myOrders.value = orders
        }?.let { disposables.add(it) }
    
        zrxRelayerAdapter?.selectedPairSubject?.subscribe { position ->
            selectedPairPosition.value = position
        }?.let { disposables.add(it) }
        
        refreshOrders()
    }
    
    private fun refreshOrders() {
        buyOrders.value = zrxRelayerAdapter?.uiBuyOrders
        sellOrders.value = zrxRelayerAdapter?.uiSellOrders
        myOrders.value = zrxRelayerAdapter?.uiMyOrders
    }
    
    fun onPickPair(position: Int) {
        zrxRelayerAdapter?.currentSelectedPair = position
    }
}
