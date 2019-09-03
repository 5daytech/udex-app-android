package com.blocksdecoded.dex.core.zrx

import android.util.Log
import com.blocksdecoded.dex.core.CancelOrderException
import com.blocksdecoded.dex.core.CreateOrderException
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.ioSubscribe
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.AssetItem
import com.blocksdecoded.zrxkit.model.Order
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.util.*
import java.util.concurrent.TimeUnit

class RelayerAdapter(
	private val coinManager: ICoinManager,
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
			(coinManager.getErcCoinForAddress(it.first.address)?.code ?: "") to
					(coinManager.getErcCoinForAddress(it.second.address)?.code ?: "")
		}
		availablePairsSubject.onNext(pairs)

		Observable.interval(refreshInterval, TimeUnit.SECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.subscribe { refreshOrders() }
			.let { disposables.add(it) }

		refreshOrders()
	}

	//region Private

	private fun getErcCoin(coinCode: String): CoinType.Erc20 =
		coinManager.getCoin(coinCode).type as CoinType.Erc20

	private fun refreshOrders() {
		relayer.availablePairs.forEachIndexed { index, pair ->
			val base = pair.first.assetData
			val quote = pair.second.assetData

			refreshPair(base, quote)
		}
	}

	private fun refreshPair(baseAsset: String, quoteAsset: String) {
		relayerManager.getOrderbook(relayerId, baseAsset, quoteAsset)
			.ioSubscribe(disposables, {
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
					.ioSubscribe(disposables, { ordersInfo ->
						myOrdersInfo.updatePairOrders(baseAsset, quoteAsset, ordersInfo)
						this.myOrders.updatePairOrders(baseAsset, quoteAsset, myOrders)
					}, {

					})
			})
	}

	private fun checkCoinAllowance(address: String): Flowable<Boolean> {
		val coinWrapper = zrxKit.getErc20ProxyInstance(address)

		return coinWrapper.proxyAllowance(ethereumKit.receiveAddress)
			.flatMap { Logger.d("$address allowance $it")
				if (it > BigInteger.ZERO) {
					Flowable.just(true)
				} else {
					coinWrapper.setUnlimitedProxyAllowance().map {
						Logger.d("$address unlocked")
						true
					}
				}
			}
	}

	private fun checkAllowance(assetPair: Pair<AssetItem, AssetItem>): Flowable<Boolean> {
		val base = assetPair.first
		val quote = assetPair.second

		return checkCoinAllowance(base.address)
			.flatMap { checkCoinAllowance(quote.address) }
	}

    private fun postOrder(
        makeAsset: String,
        makeAmount: BigInteger,
        takeAsset: String,
        takeAmount: BigInteger,
        side: EOrderSide
    ): Flowable<SignedOrder> {
        val expirationTime = ((Date().time / 1000) + (60 * 60 * 24 * 7)).toString() // Order valid for 7 days

        val makerAsset = when(side) {
            EOrderSide.BUY -> takeAsset
            else -> makeAsset
        }

        val takerAsset = when(side) {
            EOrderSide.BUY -> makeAsset
            else -> takeAsset
        }

        val order = Order(
            makerAddress = ethereumKit.receiveAddress.toLowerCase(),
            exchangeAddress = exchangeWrapper.contractAddress,
            makerAssetData = makerAsset,
            takerAssetData = takerAsset,
			makerAssetAmount = makeAmount.toString(),
			takerAssetAmount = takeAmount.toString(),
            expirationTimeSeconds = expirationTime,
            senderAddress = "0x0000000000000000000000000000000000000000",
            takerAddress = "0x0000000000000000000000000000000000000000",
            makerFee = "0",
            takerFee = "0",
            feeRecipientAddress = relayer.feeRecipients.first(),
            salt = Date().time.toString()
        )

        val signedOrder = zrxKit.signOrder(order)

        return if (signedOrder != null) {
            zrxKit.relayerManager.postOrder(0, signedOrder)
                .map { signedOrder }
        } else {
            Flowable.error(CreateOrderException())
        }
    }

	private fun getPairOrders(coinPair: Pair<String, String>, side: EOrderSide): RelayerOrders<SignedOrder> {
		val baseCoin = getErcCoin(coinPair.first)
		val quoteCoin = getErcCoin(coinPair.second)

		return when(side) {
			EOrderSide.BUY -> buyOrders
			else -> sellOrders
		}.getPair(
			ZrxKit.assetItemForAddress(baseCoin.address).assetData,
			ZrxKit.assetItemForAddress(quoteCoin.address).assetData
		)
	}

	//endregion
	
	//region Public

	override fun createOrder(
		coinPair: Pair<String, String>,
		side: EOrderSide,
		amount: BigDecimal,
		price: BigDecimal
	): Flowable<SignedOrder> {
		val baseCoin = coinManager.getCoin(coinPair.first).type as CoinType.Erc20
		val quoteCoin = coinManager.getCoin(coinPair.second).type as CoinType.Erc20

		val baseAsset = ZrxKit.assetItemForAddress(baseCoin.address)
		val quoteAsset = ZrxKit.assetItemForAddress(quoteCoin.address)

		val makerAmount = amount.movePointRight(
			if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal
		).stripTrailingZeros().toBigInteger()

		val takerAmount = amount.multiply(price).movePointRight(
			if (side == EOrderSide.BUY) baseCoin.decimal else quoteCoin.decimal
		).stripTrailingZeros().toBigInteger()

		return checkAllowance(baseAsset to quoteAsset)
			.flatMap { postOrder(baseAsset.assetData, makerAmount, quoteAsset.assetData, takerAmount, side) }
	}

	override fun fill(coinPair: Pair<String, String>, side: EOrderSide, amount: BigDecimal): Flowable<String> {
		val baseCoin = coinManager.getCoin(coinPair.first).type as CoinType.Erc20
		val quoteCoin = coinManager.getCoin(coinPair.second).type as CoinType.Erc20

		val pairOrders = when(side) {
			EOrderSide.BUY -> buyOrders
			else -> sellOrders
		}.getPair(
			ZrxKit.assetItemForAddress(baseCoin.address).assetData,
			ZrxKit.assetItemForAddress(quoteCoin.address).assetData
		)

		val calcAmount = amount.movePointRight(baseCoin.decimal)
			.stripTrailingZeros()

		Log.d("ololo", "Fill amount ${amount.toPlainString()} - ${pairOrders.orders.toString()}")

		return checkAllowance(ZrxKit.assetItemForAddress(baseCoin.address) to ZrxKit.assetItemForAddress(quoteCoin.address))
			.flatMap { exchangeWrapper.marketBuyOrders(pairOrders.orders, calcAmount.toBigInteger()) }
	}

	override fun cancelOrder(order: SignedOrder): Flowable<String> =
		if (order.makerAddress.equals(ethereumKit.receiveAddress, true)) {
			exchangeWrapper.cancelOrder(order)
		} else {
			Flowable.error(CancelOrderException(order.makerAddress, ethereumKit.receiveAddress))
		}

	override fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal = try {
		val baseCoin = getErcCoin(coinPair.first)
		val quoteCoin = getErcCoin(coinPair.second)

		val pairOrders = getPairOrders(coinPair, side)

		val makerAmount = pairOrders.orders.first().makerAssetAmount.toBigDecimal()
			.movePointLeft(if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal)
			.stripTrailingZeros()

		val takerAmount = pairOrders.orders.first().takerAssetAmount.toBigDecimal()
			.movePointLeft(if (side == EOrderSide.BUY) baseCoin.decimal else quoteCoin.decimal)
			.stripTrailingZeros()

		val math = MathContext.DECIMAL64
		val price = makerAmount.divide(takerAmount, math)
			.stripTrailingZeros()

		price
	} catch (e: Exception) {
		BigDecimal.ZERO
	}

	override fun calculateFillAmount(coinPair: Pair<String, String>, side: EOrderSide, amount: BigDecimal): BigDecimal = try {
		val pairOrders = getPairOrders(coinPair, side)

		val price = calculateBasePrice(coinPair, side)
		amount.multiply(price)
	} catch (e: Exception) {
		BigDecimal.ZERO
	}

	override fun stop() {
		disposables.clear()
		buyOrders.clear()
		sellOrders.clear()
		myOrders.clear()
		myOrdersInfo.clear()
	}

	//endregion
}