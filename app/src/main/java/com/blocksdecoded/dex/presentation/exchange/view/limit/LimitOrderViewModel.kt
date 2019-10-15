package com.blocksdecoded.dex.presentation.exchange.view.limit

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.zrx.model.CreateOrderData
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.BaseExchangeViewModel
import com.blocksdecoded.dex.presentation.exchange.model.ExchangeCoinItem
import com.blocksdecoded.dex.presentation.exchange.model.LimitOrderViewState
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.normalizedDiv
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class LimitOrderViewModel: BaseExchangeViewModel<LimitOrderViewState>() {

	private val ratesConverter = App.ratesConverter
	private val ratesManager = App.ratesManager

	override var state: LimitOrderViewState =
        LimitOrderViewState(
            BigDecimal.ZERO,
            null,
            null
        )

	private val mPriceInfo = AmountInfo(BigDecimal.ZERO)

	val averagePrice = MutableLiveData<BigDecimal>()
	val priceInfo = MutableLiveData<AmountInfo>()

	init {
		init()

		ratesManager.marketsStateSubject
			.subscribe { refreshAveragePrice() }
			.let { disposables.add(it) }
	}

	private fun refreshAveragePrice() {
		val price = if (state.sendCoin != null && state.receiveCoin != null) {
			val sendCoin = state.sendCoin?.code ?: ""
			val receiveCoin = state.receiveCoin?.code ?: ""
			ratesConverter.getCoinDiff(sendCoin, receiveCoin)
		} else {
			BigDecimal.ZERO
		}

		averagePrice.postValue(price)
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
			val receiveAmount = amount.multiply(mPriceInfo.value)
			mReceiveInfo.amount = receiveAmount

			receiveInfo.value = mReceiveInfo

			exchangeEnabled.value = receiveAmount > BigDecimal.ZERO

			exchangePrice.value = relayer?.calculateBasePrice(
				marketCodes[currentMarketPosition],
				orderSide
			) ?: BigDecimal.ZERO
		}
	}
	
	override fun initState(sendItem: ExchangeCoinItem?, receiveItem: ExchangeCoinItem?) {
		state = LimitOrderViewState(
            BigDecimal.ZERO,
            sendItem,
            receiveItem
        )
		viewState.postValue(state)

		mReceiveInfo.amount = BigDecimal.ZERO
		receiveInfo.postValue(mReceiveInfo)

		mPriceInfo.value = BigDecimal.ZERO
		priceInfo.postValue(mPriceInfo)

		averagePrice.postValue(BigDecimal.ZERO)

		refreshAveragePrice()
	}
	
	private fun placeOrder() {
		state.sendAmount.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.value > BigDecimal.ZERO) {
				messageEvent.postValue(R.string.message_order_creating)
				showProcessingEvent.call()

				val orderData = CreateOrderData(
					marketCodes[currentMarketPosition],
					if (orderSide == EOrderSide.BUY) EOrderSide.SELL else EOrderSide.BUY,
					amount,
					mPriceInfo.value
				)
				relayer?.createOrder(orderData)
					?.uiSubscribe(disposables, {}, {
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
		val confirmInfo = ExchangeConfirmInfo(
			state.sendCoin?.code ?: "",
			state.receiveCoin?.code ?: "",
			state.sendAmount,
			mReceiveInfo.amount
		) { placeOrder() }

		confirmEvent.value = confirmInfo
	}
	
	//endregion
	
	//region Public
	
	fun onPriceChange(price: BigDecimal) {
		if (mPriceInfo.value != price) {
			mPriceInfo.value = price
			
			updateReceiveAmount()
		}
	}
	
	fun onExchangeClick() {
		viewState.value?.sendAmount?.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.value > BigDecimal.ZERO) {
				showConfirm()
			} else {
				errorEvent.postValue(R.string.message_invalid_amount)
			}
		}
	}
	
	fun onSwitchClick() {
		var currentReceive = mReceiveInfo.amount
		var currentSend = state.sendAmount

		val currentPrice = if (mReceiveInfo.amount > BigDecimal.ZERO) {
			state.sendAmount.normalizedDiv(mReceiveInfo.amount)
		} else {
			currentReceive = currentSend
			currentSend = BigDecimal.ZERO
			BigDecimal.ZERO
		}

		state = LimitOrderViewState(
            sendAmount = currentReceive,
            sendCoin = state.receiveCoin,
            receiveCoin = state.sendCoin
        )

		mReceiveInfo.amount = currentSend
		mPriceInfo.value = currentPrice

		refreshPairs(state)

		viewState.value = state
		priceInfo.postValue(mPriceInfo)
		receiveInfo.postValue(mReceiveInfo)

		refreshAveragePrice()
	}
	
	//endregion
}