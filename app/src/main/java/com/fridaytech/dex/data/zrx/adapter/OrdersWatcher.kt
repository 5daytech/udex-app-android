package com.fridaytech.dex.data.zrx.adapter

import com.fridaytech.dex.data.manager.ICoinManager
import com.fridaytech.dex.data.manager.rates.RatesConverter
import com.fridaytech.dex.data.zrx.IRelayerAdapter
import com.fridaytech.dex.data.zrx.model.ExchangePair
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.presentation.orders.model.EOrderSide.*
import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.model.SignedOrder
import com.fridaytech.zrxkit.relayer.model.OrderRecord
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigInteger

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

    var simpleBuyOrders: List<SimpleOrder> = listOf()
    val buyOrdersSubject: BehaviorSubject<List<SimpleOrder>> = BehaviorSubject.create()

    var simpleSellOrders: List<SimpleOrder> = listOf()
    val sellOrdersSubject: BehaviorSubject<List<SimpleOrder>> = BehaviorSubject.create()

    var simpleMyOrders: List<SimpleOrder> = listOf()
    val myOrdersSubject: BehaviorSubject<List<SimpleOrder>> = BehaviorSubject.create()

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

        relayerAdapter.myOrdersSyncSubject.subscribe {
            refreshMyOrders(relayerAdapter.myOrders)
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
        refreshMyOrders(relayerAdapter.myOrders)
    }

    private fun refreshBuyOrders(orders: List<OrderRecord>) {
        simpleBuyOrders = orders
            .map { SimpleOrder.fromOrder(coinManager, ratesConverter, it, BUY) }
            .sortedBy { it.price }

        buyOrdersSubject.onNext(simpleBuyOrders)
    }

    private fun refreshSellOrders(orders: List<OrderRecord>) {
        simpleSellOrders = orders
            .map { SimpleOrder.fromOrder(coinManager, ratesConverter, it, SELL) }
            .sortedBy { it.price }

        sellOrdersSubject.onNext(simpleSellOrders)
    }

    private fun refreshMyOrders(myOrders: List<OrderRecord>) = try {
        simpleMyOrders = myOrders.mapIndexed { index, it ->
            val orderInfo = OrderInfo("", "", BigInteger.ZERO)
            SimpleOrder.fromOrder(
                coinManager,
                ratesConverter,
                it,
                MY,
                isMine = true,
                orderInfo = orderInfo
            )
        }
        myOrdersSubject.onNext(simpleMyOrders)
    } catch (e: Exception) {
// 		Logger.e(e)
    }

    private fun isSelectedPair(baseAsset: String, quoteAsset: String): Boolean =
        getCurrentExchangePair().baseAsset.assetData.equals(baseAsset, true) &&
                getCurrentExchangePair().quoteAsset.assetData.equals(quoteAsset, true)

    // TODO: Replace with order hash
    fun getMyOrder(position: Int, side: EOrderSide): Triple<OrderRecord, OrderInfo, EOrderSide>? = when (side) {
        MY -> {
            val myOrder = relayerAdapter.myOrders[position]

            Triple(myOrder, OrderInfo("", "", BigInteger.ZERO), MY)
        }
        else -> null
    }

    fun getMyOrders(): List<SignedOrder>? = relayerAdapter.myOrders.map { it.order }

    fun start() {
    }

    fun stop() {
        disposables.clear()
    }
}
