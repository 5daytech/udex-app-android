package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.zrxkit.model.AssetItem
import com.blocksdecoded.zrxkit.model.OrderInfo
import com.blocksdecoded.zrxkit.model.SignedOrder
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

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
	
	fun calculateQuotePrice(amount: BigDecimal): BigDecimal
}