package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.rates.RatesConverter
import com.blocksdecoded.dex.core.manager.zrx.model.ExchangePair
import com.blocksdecoded.dex.core.manager.zrx.model.RelayerOrders
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import com.blocksdecoded.zrxkit.relayer.model.OrderRecord
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class OrdersWatcher(
	private val coinManager: ICoinManager,
	private val relayerAdapter: IRelayerAdapter,
	private val ratesConverter: RatesConverter
) {
	private val disposables = CompositeDisposable()

	var currentSelectedPair: Int = 0
        get() {
            if (field >= relayerAdapter.exchangePairs.size) {
                field = 0
            }

            return field
        }
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
				refreshSellOrders(it.orders)
			}
		}.let { disposables.add(it) }

		relayerAdapter.buyOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshBuyOrders(it.orders)
			}
		}.let { disposables.add(it) }

		relayerAdapter.myOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshMyOrders(it)
			}
		}.let { disposables.add(it) }
	}

	private fun getCurrentExchangePair(): ExchangePair {
		val selectedPairOutOfBounds = currentSelectedPair >= relayerAdapter.exchangePairs.size

		if (selectedPairOutOfBounds) {
			currentSelectedPair = 0
		}

		return relayerAdapter.exchangePairs[currentSelectedPair]
	}
	
	private fun updateCachedOrders() {
		val base = getCurrentExchangePair().baseAsset.assetData
		val quote = getCurrentExchangePair().quoteAsset.assetData
		
		refreshBuyOrders(relayerAdapter.buyOrders.getPair(base, quote).orders)
		refreshSellOrders(relayerAdapter.sellOrders.getPair(base, quote).orders)
		refreshMyOrders(relayerAdapter.myOrders.getPair(base, quote))
	}

	private fun refreshBuyOrders(orders: List<OrderRecord>) {
		uiBuyOrders = orders
			.map { UiOrder.fromOrder(coinManager, ratesConverter, it.order, BUY) }
			.sortedBy { it.price }

		buyOrdersSubject.onNext(uiBuyOrders)
	}

	private fun refreshSellOrders(orders: List<OrderRecord>) {
		uiSellOrders = orders
			.map { UiOrder.fromOrder(coinManager, ratesConverter, it.order, SELL) }
			.sortedBy { it.price }

		sellOrdersSubject.onNext(uiSellOrders)
	}

	private fun refreshMyOrders(pairOrders: RelayerOrders<Pair<SignedOrder, EOrderSide>>) = try {
		uiMyOrders = pairOrders.orders
			.mapIndexed { index, it ->
				UiOrder.fromOrder(
					coinManager,
					ratesConverter,
					it.first,
					it.second,
					isMine = true,
					orderInfo = relayerAdapter.myOrdersInfo.getPair(pairOrders.baseAsset, pairOrders.quoteAsset).orders[index]
				)
			}
		myOrdersSubject.onNext(uiMyOrders)
	} catch (e: Exception) {
//		Logger.e(e)
	}

	private fun getMySelectedOrders(): RelayerOrders<Pair<SignedOrder, EOrderSide>> =
		relayerAdapter.myOrders.getPair(
			getCurrentExchangePair().baseAsset.assetData,
			getCurrentExchangePair().quoteAsset.assetData
		)

	private fun getSelectedOrdersInfo(): RelayerOrders<OrderInfo> =
		relayerAdapter.myOrdersInfo.getPair(
			getCurrentExchangePair().baseAsset.assetData,
			getCurrentExchangePair().quoteAsset.assetData
		)
	
	private fun isSelectedPair(baseAsset: String, quoteAsset: String): Boolean =
		getCurrentExchangePair().baseAsset.assetData.equals(baseAsset, true) &&
				getCurrentExchangePair().quoteAsset.assetData.equals(quoteAsset, true)
	
	//TODO: Replace with order hash
	fun getMyOrder(position: Int, side: EOrderSide): Triple<SignedOrder, OrderInfo, EOrderSide>? = when(side) {
		MY -> {
			val myOrder = getMySelectedOrders().orders[position]
			val orderInfo = getSelectedOrdersInfo().orders[position]

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