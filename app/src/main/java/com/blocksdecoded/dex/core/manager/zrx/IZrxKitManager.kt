package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.AssetItem
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

interface IZrxKitManager {
    fun zrxKit(): ZrxKit
}

interface IRelayerAdapterManager {
    val refreshInterval: Long
    var mainRelayer: IRelayerAdapter?
    val mainRelayerUpdatedSignal: BehaviorSubject<Unit>

    fun refresh()
    fun initRelayer()

    fun clearRelayers()
}

interface IRelayerAdapter {
    val refreshInterval: Long
    val relayerId: Int

    var buyOrders: RelayerOrdersList<SignedOrder>
    var myOrdersInfo: RelayerOrdersList<OrderInfo>
    var sellOrders: RelayerOrdersList<SignedOrder>
    var myOrders: RelayerOrdersList<Pair<SignedOrder, EOrderSide>>

    val availablePairs: List<Pair<AssetItem, AssetItem>>
    val availablePairsSubject: BehaviorSubject<List<Pair<String, String>>>

    fun stop()

    fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal

    fun calculateFillAmount(coinPair: Pair<String, String>, side: EOrderSide, amount: BigDecimal): BigDecimal

    fun fill(coinPair: Pair<String, String>, side: EOrderSide, amount: BigDecimal): Flowable<String>

    fun createOrder(
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal,
        price: BigDecimal
    ): Flowable<SignedOrder>

    fun cancelOrder(order: SignedOrder): Flowable<String>
}