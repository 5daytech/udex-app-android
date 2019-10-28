package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.core.manager.zrx.model.*
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.zrxkit.ZrxKit
import com.blocksdecoded.zrxkit.model.AssetItem
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import com.blocksdecoded.zrxkit.relayer.model.OrderRecord
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

interface IZrxKitManager {
    fun zrxKit(): ZrxKit
}

interface IAllowanceChecker {
    fun enableAllowance(address: String): Flowable<Boolean>

    fun enableAssetPairAllowance(assetPair: Pair<AssetItem, AssetItem>): Flowable<Boolean>

    fun enablePairAllowance(pair: Pair<String, String>): Flowable<Boolean>
}

interface IExchangeInteractor {
    fun fill(orders: List<SignedOrder>, fillData: FillOrderData): Flowable<String>

    fun createOrder(feeRecipient: String, createData: CreateOrderData): Flowable<SignedOrder>

    fun cancelOrder(order: SignedOrder): Flowable<String>

    fun ordersInfo(orders: List<SignedOrder>): Flowable<List<OrderInfo>>
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

    var myOrders: RelayerOrdersList<Pair<SignedOrder, EOrderSide>>
    var myOrdersInfo: RelayerOrdersList<OrderInfo>

    var buyOrders: RelayerOrdersList<OrderRecord>
    var sellOrders: RelayerOrdersList<OrderRecord>

    val allPairs: List<Pair<AssetItem, AssetItem>>
    val exchangePairs: List<ExchangePair>
    val pairsUpdateSubject: BehaviorSubject<Unit>

    fun stop()

    fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal

    fun calculateFillAmount(
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal
    ): FillResult

    fun calculateSendAmount(
        coinPair: Pair<String, String>,
        side: EOrderSide,
        amount: BigDecimal
    ): FillResult

    fun fill(fillData: FillOrderData): Flowable<String>

    fun createOrder(createData: CreateOrderData): Flowable<SignedOrder>

    fun cancelOrder(order: SignedOrder): Flowable<String>
}