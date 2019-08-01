package com.blocksdecoded.dex.presentation.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.subscribeUi
import com.blocksdecoded.zrxkit.contracts.ZrxExchangeWrapper
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class OrdersViewModel : CoreViewModel() {
    private val updateInterval = 10L

    private val etherKit = App.ethereumKitManager.defaultKit()
    private val zrxKit = App.zrxKitManager.zrxKit()
    private val relayerManager = zrxKit.relayerManager
    private val relayer = relayerManager.availableRelayers.first()
    
    private val buyOrders = MutableLiveData<List<SignedOrder>>()
    private val sellOrders = MutableLiveData<List<SignedOrder>>()
    
    private val myOrders = MutableLiveData<List<Pair<SignedOrder, EOrderSide>>>()
    private val myOrdersInfo = MutableLiveData<List<OrderInfo>>()
    
    private val exchangeWrapper: ZrxExchangeWrapper = zrxKit.getExchangeInstance()
    
    val uiBuyOrders: LiveData<List<UiOrder>> = Transformations.map(buyOrders) {
        it.map { UiOrder.fromOrder(it, EOrderSide.BUY) }.sortedByDescending { it.price }
    }
    
    val uiSellOrders: LiveData<List<UiOrder>> = Transformations.map(sellOrders) {
        it.map { UiOrder.fromOrder(it, EOrderSide.SELL) }.sortedBy { it.price }
    }
    
    val uiMyOrders: LiveData<List<UiOrder>> = Transformations.map(myOrders) {
        it.mapIndexed { index, it ->
            UiOrder.fromOrder(it.first, it.second, isMine = true, orderInfo = myOrdersInfo.value?.get(index))
        }
    }

    private var currentPair: Int = 0
        set(value) {
            field = value
        
            buyOrders.value = listOf()
            sellOrders.value = listOf()
            myOrders.value = listOf()
        
            refreshOrders(value)
        }
    
    val availablePairs = MutableLiveData<List<Pair<String, String>>>()

    init {
        //TODO: Refactor
        availablePairs.value = relayer.availablePairs.map {
            (CoinManager.getErcCoinForAddress(it.first.address)?.code ?: "") to (CoinManager.getErcCoinForAddress(it.second.address)?.code ?: "")
        }
        
        Observable.interval(updateInterval, TimeUnit.SECONDS)
            .subscribe { refreshOrders(currentPair) }
            .let { disposables.add(it) }
    }

    private fun refreshOrders(pairPosition: Int) {
        if (relayer.availablePairs.isValidIndex(pairPosition)) {
            val baseCoin = relayer.availablePairs[pairPosition].first.assetData
            val quoteCoin = relayer.availablePairs[pairPosition].second.assetData
    
            relayerManager.getOrderbook(0, baseCoin, quoteCoin)
                .subscribeUi(disposables, {
                    buyOrders.value = it.bids.records.map { it.order }
                    sellOrders.value = it.asks.records.map { it.order }
            
                    val address = etherKit.receiveAddress.toLowerCase()
            
                    val orders = it.asks.records
                        .map { it.order }
                        .filter { it.makerAddress.equals(address, true) }
                        .map { it to EOrderSide.SELL }
                        .plus(it.bids.records.map { it.order }.filter { it.makerAddress.equals(address, true) }.map { it to EOrderSide.BUY })
            
                    exchangeWrapper.ordersInfo(orders.map { it.first })
                        .subscribeUi({
                            myOrdersInfo.value = it
                            myOrders.value = orders
                        }, {
                        
                        })
                })
        }
    }
    
    fun onPickPair(position: Int) {
        if (currentPair != position) currentPair = position
    }
}
