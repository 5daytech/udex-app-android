package com.blocksdecoded.dex.presentation.exchange.view.limit

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class LimitOrderViewModel: BaseExchangeViewModel<LimitOrderViewState>() {
	override var state: LimitOrderViewState = LimitOrderViewState(
		BigDecimal.ZERO,
		null,
		null
	)

	private val mPriceInfo = ExchangePriceInfo(BigDecimal.ZERO)

	val priceInfo = MutableLiveData<ExchangePriceInfo>()

	init {
		init()
	}

	//region Private
	
	override fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?) {
		state = LimitOrderViewState(
			BigDecimal.ZERO,
			sendItem,
			receiveItem
		)
		viewState.value = state

		mReceiveInfo.receiveAmount = BigDecimal.ZERO
		receiveInfo.value = mReceiveInfo

		mPriceInfo.sendPrice = BigDecimal.ZERO
		priceInfo.value = mPriceInfo
	}
	
	private fun updateReceiveAmount() {
		state.sendAmount.let { amount ->
			val receiveAmount = amount.multiply(mPriceInfo.sendPrice)
			mReceiveInfo.receiveAmount = receiveAmount

			receiveInfo.value = mReceiveInfo
			
			exchangeEnabled.value = receiveAmount > BigDecimal.ZERO

			exchangePrice.value = relayer.calculateBasePrice(
				marketCodes[currentMarketPosition],
				if (exchangeSide == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL
			)
		}
	}
	
	private fun placeOrder() {
		state.sendAmount.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.sendPrice > BigDecimal.ZERO) {
				messageEvent.postValue(R.string.message_order_creating)
				
				relayer.createOrder(
					marketCodes[currentMarketPosition],
					if (exchangeSide == ExchangeSide.BID) EOrderSide.SELL else EOrderSide.BUY,
					amount,
					mPriceInfo.sendPrice
				).uiSubscribe(disposables, {}, {
					errorEvent.postValue(R.string.error_order_place)
					Logger.e(it)
				}, {
					messageEvent.postValue(R.string.message_order_created)
					initState(state.sendPair, state.receivePair)
				})
				
			} else {
				messageEvent.postValue(R.string.message_invalid_amount)
			}
		}
	}
	
	private fun showConfirm() {
		val pair = marketCodes[currentMarketPosition]
		
		val confirmInfo = ExchangeConfirmInfo(
			pair.first,
			pair.second,
			state.sendAmount,
			mReceiveInfo.receiveAmount
		) { placeOrder() }

		confirmEvent.value = confirmInfo
	}
	
	//endregion
	
	//region Public
	
	fun onReceiveCoinPick(position: Int) {
		state.receivePair = mReceiveCoins[position]
		updateReceiveAmount()
	}
	
	fun onSendCoinPick(position: Int) {
		state.sendPair = mSendCoins[position]
		refreshPairs(state, false)
		updateReceiveAmount()
	}
	
	override fun onSendAmountChange(amount: BigDecimal) {
		if (state.sendAmount != amount) {
			state.sendAmount = amount
			
			updateReceiveAmount()
		}
	}
	
	fun onPriceChange(price: BigDecimal) {
		if (mPriceInfo.sendPrice != price) {
			mPriceInfo.sendPrice = price
			
			updateReceiveAmount()
		}
	}
	
	fun onExchangeClick() {
		viewState.value?.sendAmount?.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.sendPrice > BigDecimal.ZERO) {
				showConfirm()
			} else {
				errorEvent.postValue(R.string.message_invalid_amount)
			}
		}
	}
	
	fun onSwitchClick() {
		exchangeSide = when(exchangeSide) {
			ExchangeSide.BID -> ExchangeSide.ASK
			ExchangeSide.ASK -> ExchangeSide.BID
		}

		val currentReceive = mReceiveInfo.receiveAmount
		val currentPrice = if (mReceiveInfo.receiveAmount > BigDecimal.ZERO) {
			state.sendAmount.divide(mReceiveInfo.receiveAmount) ?: BigDecimal.ZERO
		} else {
			BigDecimal.ZERO
		}

		val currentSend = state.sendAmount

		state = LimitOrderViewState(
			sendAmount = currentReceive,
			sendPair = state.receivePair,
			receivePair = state.sendPair
		)

		mReceiveInfo.receiveAmount = currentSend
		mPriceInfo.sendPrice = currentPrice

		refreshPairs(state)

		viewState.value = state
		receiveInfo.value = mReceiveInfo
		priceInfo.value = mPriceInfo
	}
	
	//endregion
}