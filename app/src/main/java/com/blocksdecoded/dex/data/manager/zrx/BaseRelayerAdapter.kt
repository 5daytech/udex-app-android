package com.blocksdecoded.dex.data.manager.zrx

import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.data.manager.ICoinManager
import com.blocksdecoded.dex.data.manager.zrx.model.*
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.normalizedMul
import com.blocksdecoded.dex.utils.rx.ioSubscribe
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import com.blocksdecoded.zrxkit.relayer.model.OrderRecord
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BaseRelayerAdapter(
    private val coinManager: ICoinManager,
    private val ethereumKit: EthereumKit,
    private val exchangeInteractor: IExchangeInteractor,
    zrxKit: ZrxKit,
    override val refreshInterval: Long,
    override val relayerId: Int
) : IRelayerAdapter {
    private val disposables = CompositeDisposable()

    private val relayerManager = zrxKit.relayerManager
    private val relayer = relayerManager.availableRelayers[relayerId]

    override var myOrdersInfo = RelayerOrdersList<OrderInfo>()
    override var buyOrders = RelayerOrdersList<OrderRecord>()
    override var sellOrders = RelayerOrdersList<OrderRecord>()
    override var myOrders = RelayerOrdersList<Pair<SignedOrder, EOrderSide>>()

    override val allPairs = relayer.availablePairs
    override var exchangePairs: List<ExchangePair> = listOf()
    override val pairsUpdateSubject: BehaviorSubject<Unit> = BehaviorSubject.create()

    init {
        initPairs()

        coinManager.coinsUpdatedSubject.subscribe {
            initPairs()
        }.let { disposables.add(it) }

        Observable.interval(refreshInterval, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { refreshOrders() }
            .let { disposables.add(it) }
    }

    //region Private

    private fun initPairs() {
        exchangePairs = allPairs.filter {
            val baseCoin = coinManager.getErcCoinForAddress(it.first.address)
            val quoteCoin = coinManager.getErcCoinForAddress(it.second.address)

            baseCoin != null && quoteCoin != null
        }.map {
            ExchangePair(
                (coinManager.getErcCoinForAddress(it.first.address)?.code ?: ""),
                (coinManager.getErcCoinForAddress(it.second.address)?.code ?: ""),
                it.first,
                it.second
            )
        }

        pairsUpdateSubject.onNext(Unit)
        refreshOrders()
    }

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
                val myAddress = ethereumKit.receiveAddress.toLowerCase()

                buyOrders.updatePairOrders(baseAsset, quoteAsset, it.bids.records.filterNot { it.order.makerAddress.equals(myAddress, false) })
                sellOrders.updatePairOrders(baseAsset, quoteAsset, it.asks.records.filterNot { it.order.makerAddress.equals(myAddress, false) })

                val myOrders = it.asks.records
                    .map { it.order }
                    .filter { it.makerAddress.equals(myAddress, true) }
                    .map { it to EOrderSide.SELL }
                    .plus(it.bids.records.map { it.order }.filter { it.makerAddress.equals(myAddress, true) }.map { it to EOrderSide.BUY })

                this.myOrders.updatePairOrders(baseAsset, quoteAsset, myOrders)

                exchangeInteractor.ordersInfo(myOrders.map { it.first })
                    .ioSubscribe(disposables, { ordersInfo ->
                        myOrdersInfo.updatePairOrders(baseAsset, quoteAsset, ordersInfo)
                        this.myOrders.updatePairOrders(baseAsset, quoteAsset, myOrders)
                    }, { })
            })
    }

    private fun getPairOrders(coinPair: Pair<String, String>, side: EOrderSide): RelayerOrders<OrderRecord> {
        val baseCoin = getErcCoin(coinPair.first)
        val quoteCoin = getErcCoin(coinPair.second)

        return when (side) {
            EOrderSide.BUY -> buyOrders
            else -> sellOrders
        }.getPair(
            ZrxKit.assetItemForAddress(baseCoin.address).assetData,
            ZrxKit.assetItemForAddress(quoteCoin.address).assetData
        )
    }

    private fun calculateFillResult(
        orders: List<OrderRecord>,
        side: EOrderSide,
        amount: BigDecimal
    ): FillResult = try {
        val ordersToFill = arrayListOf<SignedOrder>()

        var requestedAmount = amount
        var fillAmount = BigDecimal.ZERO

        val sortedOrders = orders.map { OrdersUtil.normalizeOrderDataPrice(it) }
            .apply { if (side == EOrderSide.BUY) sortedByDescending { it.price } else sortedBy { it.price } }

        for (orderData in sortedOrders) {
            if (requestedAmount != BigDecimal.ZERO) {
                if (requestedAmount >= orderData.takerAmount) {
                    fillAmount += orderData.makerAmount
                    requestedAmount -= orderData.takerAmount
                } else {
                    fillAmount += requestedAmount.normalizedMul(orderData.price)
                    requestedAmount = BigDecimal.ZERO
                }

                orderData.order?.let { ordersToFill.add(it) }
            } else {
                break
            }
        }

        FillResult(ordersToFill, fillAmount, amount - requestedAmount)
    } catch (e: Exception) {
        Logger.e(e)
        FillResult.empty()
    }
    //endregion

    //region Public

    //region Exchange

    override fun createOrder(createData: CreateOrderData): Flowable<SignedOrder> =
        exchangeInteractor.createOrder(relayer.feeRecipients.first(), createData)

    override fun fill(fillData: FillOrderData): Flowable<String> {
        val ordersRecords = getPairOrders(fillData.coinPair, fillData.side).orders
        val fillResult = calculateFillResult(
            ordersRecords,
            fillData.side,
            fillData.amount
        )

        return exchangeInteractor.fill(fillResult.orders, fillData)
    }

    override fun cancelOrder(order: SignedOrder): Flowable<String> =
        exchangeInteractor.cancelOrder(order)

    override fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal = try {
        OrdersUtil.calculateBasePrice(
            getPairOrders(coinPair, side).orders.map { it.order },
            coinPair,
            side
        )
    } catch (e: Exception) {
        Logger.e(e)
        BigDecimal.ZERO
    }

    //endregion

    override fun calculateFillAmount(
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal
    ): FillResult = try {
        val orders = getPairOrders(coinPair, side).orders
        calculateFillResult(orders, side, amount)
    } catch (e: Exception) {
        Logger.e(e)
        FillResult.empty()
    }

    override fun calculateSendAmount(
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal
    ): FillResult = try {
        val orders = getPairOrders(coinPair, side).orders
        val ordersToFill = arrayListOf<SignedOrder>()

        var requestedAmount = amount
        var fillAmount = BigDecimal.ZERO

        val sortedOrders = orders.map { OrdersUtil.normalizeOrderDataPrice(it, isSellPrice = false) }
            .apply { if (side == EOrderSide.BUY) sortedByDescending { it.price } else sortedBy { it.price } }

        for (order in sortedOrders) {
            if (requestedAmount != BigDecimal.ZERO) {
                if (requestedAmount >= order.makerAmount) {
                    fillAmount += order.takerAmount
                    requestedAmount -= order.makerAmount
                } else {
                    fillAmount += requestedAmount.normalizedMul(order.price)
                    requestedAmount = BigDecimal.ZERO
                }
            } else {
                break
            }
        }

        FillResult(ordersToFill, amount - requestedAmount, fillAmount)
    } catch (e: Exception) {
        FillResult.empty()
    }

    override fun stop() {
        disposables.clear()
        buyOrders.clear()
        sellOrders.clear()
        myOrders.clear()
        myOrdersInfo.clear()
        exchangePairs = listOf()
    }

    //endregion
}
