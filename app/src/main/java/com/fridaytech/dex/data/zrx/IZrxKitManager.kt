package com.fridaytech.dex.data.zrx

import com.fridaytech.dex.data.zrx.model.*
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.zrxkit.ZrxKit
import com.fridaytech.zrxkit.model.AssetItem
import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.model.SignedOrder
import com.fridaytech.zrxkit.relayer.model.OrderRecord
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

interface IZrxKitManager {
    fun zrxKit(): ZrxKit
}

interface IAllowanceChecker {
    fun checkAndUnlockAssetPairForPost(assetPair: Pair<AssetItem, AssetItem>, side: EOrderSide): Flowable<Boolean>

    fun checkAndUnlockPairForFill(pair: Pair<String, String>, side: EOrderSide): Flowable<Boolean>
}

interface IExchangeInteractor {
    fun fill(orders: List<SignedOrder>, fillData: FillOrderData): Flowable<String>

    fun createOrder(feeRecipient: String, createData: CreateOrderData): Flowable<SignedOrder>

    fun cancelOrder(order: SignedOrder): Flowable<String>

    fun batchCancelOrders(orders: List<SignedOrder>): Flowable<String>

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

    var myOrders: List<OrderRecord>
    var myOrdersInfo: List<OrderInfo>
    val myOrdersSyncSubject: BehaviorSubject<Unit>

    var buyOrders: RelayerOrdersList<OrderRecord>
    var sellOrders: RelayerOrdersList<OrderRecord>

    val allPairs: List<Pair<AssetItem, AssetItem>>
    val exchangePairs: List<ExchangePair>
    val pairsSyncSubject: BehaviorSubject<Unit>

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

    fun batchCancelOrders(orders: List<SignedOrder>): Flowable<String>
}
