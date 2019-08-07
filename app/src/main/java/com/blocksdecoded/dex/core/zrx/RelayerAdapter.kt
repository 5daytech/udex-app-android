package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.subscribeUi
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class RelayerAdapter(
	private val ethereumKit: EthereumKit,
	private val zrxKit: ZrxKit,
	override val refreshInterval: Long,
	override val relayerId: Int
): IRelayerAdapter {
	private val disposables = CompositeDisposable()
	
	private val relayerManager = zrxKit.relayerManager
	private val relayer = relayerManager.availableRelayers[relayerId]
	private val availablePairs = relayer.availablePairs
	private val exchangeWrapper = zrxKit.getExchangeInstance()
	
	private var myOrdersInfo = RelayerOrdersList<OrderInfo>()
	private var buyOrders = RelayerOrdersList<SignedOrder>()
	private var sellOrders = RelayerOrdersList<SignedOrder>()
	private var myOrders = RelayerOrdersList<Pair<SignedOrder, EOrderSide>>()

	override var currentSelectedPair: Int = 0
		set(value) {
			if (field == value) return
			field = value
			selectedPairSubject.onNext(value)
			updateCachedOrders()
//			refreshOrders()
		}
	
	override val selectedPairSubject: BehaviorSubject<Int> = BehaviorSubject.create()
	override val availablePairsSubject: BehaviorSubject<List<Pair<String, String>>> =
		BehaviorSubject.create()
	
	override var buyAvailableAmount: BigDecimal = BigDecimal.ZERO
	override var uiBuyOrders: List<UiOrder> = listOf()
	override val buyOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	override var sellAvailableAmount: BigDecimal = BigDecimal.ZERO
	override var uiSellOrders: List<UiOrder> = listOf()
	override val sellOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	override var uiMyOrders: List<UiOrder> = listOf()
	override val myOrdersSubject: BehaviorSubject<List<UiOrder>> = BehaviorSubject.create()
	
	init {
		val pairs = relayer.availablePairs.map {
			(CoinManager.getErcCoinForAddress(it.first.address)?.code ?: "") to (CoinManager.getErcCoinForAddress(it.second.address)?.code ?: "")
		}
		availablePairsSubject.onNext(pairs)

		sellOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshSellOrders(it)
			}
		}.let { disposables.add(it) }


		buyOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshBuyOrders(it)
			}
		}.let { disposables.add(it) }

		myOrders.pairUpdateSubject.subscribe {
			if (isSelectedPair(it.baseAsset, it.quoteAsset)) {
				refreshMyOrders(it)
			}
		}.let { disposables.add(it) }

		Observable.interval(refreshInterval, TimeUnit.SECONDS)
			.subscribe { refreshOrders() }
			.let { disposables.add(it) }

		refreshOrders()
	}

	//region Private

	private fun refreshSellOrders(pairOrders: RelayerOrders<SignedOrder>) {
		sellAvailableAmount = BigDecimal.ZERO
		uiSellOrders = pairOrders.orders
			.map { UiOrder.fromOrder(it, EOrderSide.SELL) }.sortedBy { it.price }
		
		uiSellOrders.forEach { sellAvailableAmount += it.takerAmount  }
		sellOrdersSubject.onNext(uiSellOrders)
	}

	private fun refreshBuyOrders(pairOrders: RelayerOrders<SignedOrder>) {
		buyAvailableAmount = BigDecimal.ZERO
		uiBuyOrders = pairOrders.orders
			.map { UiOrder.fromOrder(it, EOrderSide.BUY) }.sortedByDescending { it.price }
		
		uiBuyOrders.forEach { buyAvailableAmount += it.takerAmount  }
		
		buyOrdersSubject.onNext(uiBuyOrders)
	}

	private fun refreshMyOrders(pairOrders: RelayerOrders<Pair<SignedOrder, EOrderSide>>) {
		uiMyOrders = pairOrders.orders
			.mapIndexed { index, it ->
				UiOrder.fromOrder(
					it.first,
					it.second,
					isMine = true,
					orderInfo = myOrdersInfo.getPair(pairOrders.baseAsset, pairOrders.quoteAsset).orders[index]
				)
			}
		myOrdersSubject.onNext(uiMyOrders)
	}

	private fun updateCachedOrders() {
		val base = availablePairs[currentSelectedPair].first.assetData
		val quote = availablePairs[currentSelectedPair].second.assetData

		refreshBuyOrders(buyOrders.getPair(base, quote))
		refreshSellOrders(sellOrders.getPair(base, quote))
		refreshMyOrders(myOrders.getPair(base, quote))
	}

	private fun isSelectedPair(baseAsset: String, quoteAsset: String): Boolean =
		availablePairs[currentSelectedPair].first.assetData.equals(baseAsset, true) &&
				availablePairs[currentSelectedPair].second.assetData.equals(quoteAsset, true)

	private fun refreshOrders() {
		relayer.availablePairs.forEachIndexed { index, pair ->
			val base = pair.first.assetData
			val quote = pair.second.assetData

			refreshPair(base, quote)
		}
	}

	private fun refreshPair(baseAsset: String, quoteAsset: String) {
		relayerManager.getOrderbook(relayerId, baseAsset, quoteAsset)
			.subscribeUi(disposables, {
				buyOrders.updatePairOrders(baseAsset, quoteAsset, it.bids.records.map { it.order })
				sellOrders.updatePairOrders(baseAsset, quoteAsset, it.asks.records.map { it.order })

				val address = ethereumKit.receiveAddress.toLowerCase()

				val myOrders = it.asks.records
					.map { it.order }
					.filter { it.makerAddress.equals(address, true) }
					.map { it to EOrderSide.SELL }
					.plus(it.bids.records.map { it.order }.filter { it.makerAddress.equals(address, true) }.map { it to EOrderSide.BUY })

				this.myOrders.updatePairOrders(baseAsset, quoteAsset, myOrders)
				exchangeWrapper.ordersInfo(myOrders.map { it.first })
					.subscribeUi(disposables, { ordersInfo ->
						myOrdersInfo.updatePairOrders(baseAsset, quoteAsset, ordersInfo)
						this.myOrders.updatePairOrders(baseAsset, quoteAsset, myOrders)
					}, {

					})
			})
	}

	//endregion
	
	//region Public
	
	override fun stop() {
		disposables.clear()
		buyOrders.clear()
		sellOrders.clear()
		myOrders.clear()
		myOrdersInfo.clear()
	}
	
	override fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal = try {
		val baseCoin = CoinManager.getCoin(coinPair.first).type as CoinType.Erc20
		val quoteCoin = CoinManager.getCoin(coinPair.second).type as CoinType.Erc20
		
		val pairOrders = when(side) {
			EOrderSide.BUY -> buyOrders
			else -> sellOrders
		}.getPair(
			ZrxKit.assetItemForAddress(baseCoin.address).assetData,
			ZrxKit.assetItemForAddress(quoteCoin.address).assetData
		)
		
		val makerAmount = pairOrders.orders.first().makerAssetAmount.toBigDecimal()
			.movePointLeft(baseCoin.decimal)
			.stripTrailingZeros()
		
		val takerAmount = pairOrders.orders.first().takerAssetAmount.toBigDecimal()
			.movePointLeft(quoteCoin.decimal)
			.stripTrailingZeros()

		val price = makerAmount.divide(takerAmount)
		
		price
	} catch (e: Exception) {
		BigDecimal.ZERO
	}
	
	override fun calculateQuotePrice(amount: BigDecimal): BigDecimal {
		return BigDecimal.ZERO
	}
	
	//endregion
}