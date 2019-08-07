package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

interface IRelayerAdapter {
	val refreshInterval: Long
	val relayerId: Int
	
	val selectedPairSubject: BehaviorSubject<Int>
	var currentSelectedPair: Int
	
	val availablePairsSubject: BehaviorSubject<List<Pair<String, String>>>
	
	var buyAvailableAmount: BigDecimal
	var uiBuyOrders: List<UiOrder>
	val buyOrdersSubject: BehaviorSubject<List<UiOrder>>
	
	var sellAvailableAmount: BigDecimal
	var uiSellOrders: List<UiOrder>
	val sellOrdersSubject: BehaviorSubject<List<UiOrder>>
	
	var uiMyOrders: List<UiOrder>
	val myOrdersSubject: BehaviorSubject<List<UiOrder>>
	
	fun stop()
	
	fun calculateBasePrice(coinPair: Pair<String, String>, side: EOrderSide): BigDecimal
	
	fun calculateQuotePrice(amount: BigDecimal): BigDecimal
}