package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
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
	private val exchangeWrapper = zrxKit.getExchangeInstance()
	
	override var myOrdersInfo = RelayerOrdersList<OrderInfo>()
	override var buyOrders = RelayerOrdersList<SignedOrder>()
	override var sellOrders = RelayerOrdersList<SignedOrder>()
	override var myOrders = RelayerOrdersList<Pair<SignedOrder, EOrderSide>>()

	override val availablePairs = relayer.availablePairs
	override val availablePairsSubject: BehaviorSubject<List<Pair<String, String>>> =
		BehaviorSubject.create()
	
	init {
		val pairs = relayer.availablePairs.map {
			(CoinManager.getErcCoinForAddress(it.first.address)?.code ?: "") to
					(CoinManager.getErcCoinForAddress(it.second.address)?.code ?: "")
		}
		availablePairsSubject.onNext(pairs)

		Observable.interval(refreshInterval, TimeUnit.SECONDS)
			.subscribe { refreshOrders() }
			.let { disposables.add(it) }

		refreshOrders()
	}

	//region Private

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