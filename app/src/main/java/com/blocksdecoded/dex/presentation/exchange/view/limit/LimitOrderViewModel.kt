package com.blocksdecoded.dex.presentation.exchange.view.limit

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePriceInfo
import com.blocksdecoded.dex.presentation.exchange.view.LimitOrderViewState
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class LimitOrderViewModel: BaseExchangeViewModel<LimitOrderViewState>() {

	private val ratesConverter = App.ratesConverter
	private val ratesManager = App.ratesManager

	override var state: LimitOrderViewState = LimitOrderViewState(
		BigDecimal.ZERO,
		null,
		null
	)

	private val mPriceInfo = ExchangePriceInfo(
		BigDecimal.ZERO
	)

	val averagePrice = MutableLiveData<BigDecimal>()
	val priceInfo = MutableLiveData<ExchangePriceInfo>()

	init {
		init()

		ratesManager.ratesStateSubject
			.subscribe { refreshAveragePrice() }
			.let { disposables.add(it) }
	}

	private fun refreshAveragePrice() {
		val sendCoin = state.sendCoin?.code ?: ""
		val receiveCoin = state.receiveCoin?.code ?: ""

		averagePrice.postValue(ratesConverter.getCoinDiff(sendCoin, receiveCoin))
	}

	override fun refreshPairs(state: LimitOrderViewState?, refreshSendCoins: Boolean) {
		super.refreshPairs(state, refreshSendCoins)
		refreshAveragePrice()
	}

	//region Private

	override fun onReceiveCoinPick(position: Int) {
		super.onReceiveCoinPick(position)
		refreshAveragePrice()
	}

	override fun onSendCoinPick(position: Int) {
		super.onSendCoinPick(position)
		refreshAveragePrice()
	}

	override fun updateReceiveAmount() {
		state.sendAmount.let { amount ->
			val receiveAmount = amount.multiply(mPriceInfo.sendPrice)
			mReceiveInfo.receiveAmount = receiveAmount

			receiveInfo.value = mReceiveInfo

			exchangeEnabled.value = receiveAmount > BigDecimal.ZERO

			exchangePrice.value = relayer.calculateBasePrice(
				marketCodes[currentMarketPosition],
				orderSide
			)
		}
	}
	
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
		averagePrice.value = BigDecimal.ZERO
		priceInfo.value = mPriceInfo
	}
	
	private fun placeOrder() {
		state.sendAmount.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.sendPrice > BigDecimal.ZERO) {
				messageEvent.postValue(R.string.message_order_creating)
				showProcessingEvent.call()

				relayer.createOrder(
					marketCodes[currentMarketPosition],
					if (exchangeSide == ExchangeSide.BID) EOrderSide.SELL else EOrderSide.BUY,
					amount,
					mPriceInfo.sendPrice
				).uiSubscribe(disposables, {}, {
					processingDismissEvent.call()
					errorEvent.postValue(R.string.error_order_place)
					Logger.e(it)
				}, {
					processingDismissEvent.call()
					messageEvent.postValue(R.string.message_order_created)
					initState(state.sendCoin, state.receiveCoin)
				})
				
			} else {
				messageEvent.postValue(R.string.message_invalid_amount)
			}
		}
	}
	
	private fun showConfirm() {
		val pair = marketCodes[currentMarketPosition]
		
		val confirmInfo = ExchangeConfirmInfo(
			if (exchangeSide == ExchangeSide.BID) pair.first else pair.second,
			if (exchangeSide == ExchangeSide.BID) pair.second else pair.first,
			state.sendAmount,
			mReceiveInfo.receiveAmount
		) { placeOrder() }

		confirmEvent.value = confirmInfo
	}
	
	//endregion
	
	//region Public
	
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
			sendCoin = state.receiveCoin,
			receiveCoin = state.sendCoin
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