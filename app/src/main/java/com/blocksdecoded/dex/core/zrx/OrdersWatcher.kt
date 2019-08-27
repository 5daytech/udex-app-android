package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class OrdersWatcher(
	val relayerAdapter: IRelayerAdapter
) {
	private val disposables = CompositeDisposable()

	var availablePairsSubject = relayerAdapter.availablePairsSubject

	var currentSelectedPair: Int = 0
		set(value) {
			if (field == value) return
			field = value
			selectedPairSubject.onNext(value)
			updateCachedOrders()
		}

	val selectedPairSubject: BehaviorSubject<Int> = BehaviorSubject.create()
	
	var uiBuyOrders: List<UiOrder> = listOf()
	val buyOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	var uiSellOrders: List<UiOrder> = listOf()
	val sellOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	var uiMyOrders: List<UiOrder> = listOf()
	val myOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	init {
		relayerAdapter.sellOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshSellOrders(it)
			}
		}.let { disposables.add(it) }

		relayerAdapter.buyOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshBuyOrders(it)
			}
		}.let { disposables.add(it) }

		relayerAdapter.myOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshMyOrders(it)
			}
		}.let { disposables.add(it) }
	}
	
	private fun updateCachedOrders() {
		val base = relayerAdapter.availablePairs[currentSelectedPair].first.assetData
		val quote = relayerAdapter.availablePairs[currentSelectedPair].second.assetData
		
		refreshBuyOrders(relayerAdapter.buyOrders.getPair(base, quote))
		refreshSellOrders(relayerAdapter.sellOrders.getPair(base, quote))
		refreshMyOrders(relayerAdapter.myOrders.getPair(base, quote))
	}
	
	private fun refreshSellOrders(pairOrders: RelayerOrders<SignedOrder>) {
		uiSellOrders = pairOrders.orders
			.map { UiOrder.fromOrder(it, SELL) }.sortedBy { it.price }
		
		sellOrdersSubject.onNext(uiSellOrders)
	}
	
	private fun refreshBuyOrders(pairOrders: RelayerOrders<SignedOrder>) {
		uiBuyOrders = pairOrders.orders
			.map { UiOrder.fromOrder(it, BUY) }
			.sortedByDescending { it.price }
		
		buyOrdersSubject.onNext(uiBuyOrders)
	}
	
	private fun refreshMyOrders(pairOrders: RelayerOrders<Pair<SignedOrder, EOrderSide>>) = try {
		uiMyOrders = pairOrders.orders
			.mapIndexed { index, it ->
				UiOrder.fromOrder(
					it.first,
					it.second,
					isMine = true,
					orderInfo = relayerAdapter.myOrdersInfo.getPair(pairOrders.baseAsset, pairOrders.quoteAsset).orders[index]
				)
			}
		myOrdersSubject.onNext(uiMyOrders)
	} catch (e: Exception) {
		Logger.e(e)
	}

	private fun getMySelectedOrders(): RelayerOrders<Pair<SignedOrder, EOrderSide>> =
		relayerAdapter.myOrders.getPair(
			relayerAdapter.availablePairs[currentSelectedPair].first.assetData,
			relayerAdapter.availablePairs[currentSelectedPair].second.assetData
		)

	private fun getSeletecOrdersInfo(): RelayerOrders<OrderInfo> =
		relayerAdapter.myOrdersInfo.getPair(
			relayerAdapter.availablePairs[currentSelectedPair].first.assetData,
			relayerAdapter.availablePairs[currentSelectedPair].second.assetData
		)
	
	private fun isSelectedPair(baseAsset: String, quoteAsset: String): Boolean =
		relayerAdapter.availablePairs[currentSelectedPair].first.assetData.equals(baseAsset, true) &&
				relayerAdapter.availablePairs[currentSelectedPair].second.assetData.equals(quoteAsset, true)
	
	//TODO: Replace with order hash
	fun getMyOrder(position: Int, side: EOrderSide): Triple<SignedOrder, OrderInfo, EOrderSide>? = when(side) {
		MY -> {
			val myOrder = getMySelectedOrders().orders[position]
			val orderInfo = getSeletecOrdersInfo().orders[position]

			Triple(myOrder.first, orderInfo, myOrder.second)
		}
		else -> null
	}
	
	fun start() {
	
	}
	
	fun stop() {
		disposables.dispose()
	}
}