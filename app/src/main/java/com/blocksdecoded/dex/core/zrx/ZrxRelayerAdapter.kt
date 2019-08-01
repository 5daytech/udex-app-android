package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.subscribeUi
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class ZrxRelayerAdapter(
	private val ethereumKit: EthereumKit,
	private val zrxKit: ZrxKit,
	override val refreshInterval: Long,
	override val relayerId: Int
): IRelayerAdapter {
	private val disposables = CompositeDisposable()
	
	private val relayerManager = zrxKit.relayerManager
	private val relayer = relayerManager.availableRelayers[relayerId]
	private val exchangeWrapper = zrxKit.getExchangeInstance()
	
	private var myOrdersInfo = listOf<OrderInfo>()
	
	private var buyOrders = listOf<SignedOrder>()
		set(value) {
			field = value
			uiBuyOrders = value.map { UiOrder.fromOrder(it, EOrderSide.BUY) }.sortedByDescending { it.price }
			buyOrdersSubject.onNext(uiBuyOrders)
		}
	
	private var sellOrders = listOf<SignedOrder>()
		set(value) {
			field = value
			uiSellOrders = value.map { UiOrder.fromOrder(it, EOrderSide.SELL) }.sortedBy { it.price }
			sellOrdersSubject.onNext(uiSellOrders)
		}
	
	private var myOrders = listOf<Pair<SignedOrder, EOrderSide>>()
		set(value) {
			field = value
			uiMyOrders = value.mapIndexed { index, it ->
				UiOrder.fromOrder(it.first, it.second, isMine = true, orderInfo = myOrdersInfo[index])
			}
			myOrdersSubject.onNext(uiMyOrders)
		}
	
	override var currentSelectedPair: Int = 0
		set(value) {
			if (field == value) return
			
			field = value
			selectedPairSubject.onNext(value)
			
			buyOrders = listOf()
			sellOrders = listOf()
			myOrders = listOf()
			myOrdersInfo = listOf()
			
			refreshOrders(value)
		}
	
	override val selectedPairSubject: BehaviorSubject<Int> = BehaviorSubject.create()
	override val availablePairsSubject: BehaviorSubject<List<Pair<String, String>>> =
		BehaviorSubject.create()
	
	override var uiBuyOrders: List<UiOrder> = listOf()
	override val buyOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	override var uiSellOrders: List<UiOrder> = listOf()
	override val sellOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	override var uiMyOrders: List<UiOrder> = listOf()
	override val myOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	init {
		val pairs = relayer.availablePairs.map {
			(CoinManager.getErcCoinForAddress(it.first.address)?.code ?: "") to (CoinManager.getErcCoinForAddress(it.second.address)?.code ?: "")
		}
		
		availablePairsSubject.onNext(pairs)
		
		Observable.interval(refreshInterval, TimeUnit.SECONDS)
			.subscribe { refreshOrders(currentSelectedPair) }
			.let { disposables.add(it) }
	}
	
	private fun refreshOrders(pairPosition: Int) {
		if (relayer.availablePairs.isValidIndex(pairPosition)) {
			val baseCoin = relayer.availablePairs[pairPosition].first.assetData
			val quoteCoin = relayer.availablePairs[pairPosition].second.assetData
			
			relayerManager.getOrderbook(0, baseCoin, quoteCoin)
				.subscribeUi(disposables, {
					buyOrders = it.bids.records.map { it.order }
					sellOrders = it.asks.records.map { it.order }
					
					val address = ethereumKit.receiveAddress.toLowerCase()
					
					val orders = it.asks.records
						.map { it.order }
						.filter { it.makerAddress.equals(address, true) }
						.map { it to EOrderSide.SELL }
						.plus(it.bids.records.map { it.order }.filter { it.makerAddress.equals(address, true) }.map { it to EOrderSide.BUY })
					
					exchangeWrapper.ordersInfo(orders.map { it.first })
						.subscribeUi(disposables, {
							myOrdersInfo = it
							myOrders = orders
						}, {
						
						})
				})
		}
	}
	
	override fun stop() {
		disposables.clear()
	}
}