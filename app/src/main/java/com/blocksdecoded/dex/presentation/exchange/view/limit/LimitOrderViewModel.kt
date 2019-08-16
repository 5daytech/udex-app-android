package com.blocksdecoded.dex.presentation.exchange.view.limit

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.exchange.ExchangeSide
import com.blocksdecoded.dex.presentation.exchange.confirm.ExchangeConfirmInfo
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.subscribeUi
import java.math.BigDecimal

class LimitOrderViewModel: CoreViewModel() {
	private val relayer = App.relayerAdapterManager.getMainAdapter()
	private val adapterManager = App.adapterManager
	
	private var exchangeableCoins: List<Coin> = listOf()
	private var coinPairsCodes: List<Pair<String, String>> = listOf()
	private val currentPairPosition: Int
		get() {
			val sendCoin = viewState.value?.sendPair?.code ?: ""
			val receiveCoin = viewState.value?.receivePair?.code ?: ""
			
			return coinPairsCodes.indexOfFirst {
				(it.first == sendCoin && it.second == receiveCoin) ||
					(it.second == sendCoin && it.first == receiveCoin)
			}
		}
	
	private var exchangeState = ExchangeSide.BID
	
	private var mSendCoins: List<ExchangePairItem> = listOf()
		set(value) {
			field = value
			sendCoins.value = value
		}
	
	private var mReceiveCoins: List<ExchangePairItem> = listOf()
		set(value) {
			field = value
			receiveCoins.value = value
		}

	private val mPriceInfo = OrderPriceInfo(BigDecimal.ZERO)
	private val mReceiveInfo = OrderReceiveInfo(BigDecimal.ZERO)
	
	val sendCoins = MutableLiveData<List<ExchangePairItem>>()
	val receiveCoins = MutableLiveData<List<ExchangePairItem>>()
	
	val viewState = MutableLiveData<LimitOrderViewState>()
	val priceInfo = MutableLiveData<OrderPriceInfo>()
	val totalInfo = MutableLiveData<OrderReceiveInfo>()
	val exchangeEnabled = MutableLiveData<Boolean>()
	val exchangePrice = MutableLiveData<BigDecimal>()
	
	val successEvent = SingleLiveEvent<String>()
	
	val confirmEvent = SingleLiveEvent<ExchangeConfirmInfo>()
	
	init {
		exchangeEnabled.value = false
		relayer.availablePairsSubject
			.subscribe {
				coinPairsCodes = it
				
				exchangeableCoins = CoinManager.coins.filter { coin ->
					coinPairsCodes.firstOrNull { pair ->
						pair.first.equals(coin.code, true) ||
							pair.second.equals(coin.code, true)
					} != null
				}
				
				refreshPairs(null)
				
				initState(mSendCoins.first(), mReceiveCoins.first())
			}.let { disposables.add(it) }
	}
	
	//region Private
	
	private fun initState(sendItem: ExchangePairItem?, receiveItem: ExchangePairItem?) {
		viewState.value = LimitOrderViewState(
			BigDecimal.ZERO,
			sendItem,
			receiveItem
		)

		mReceiveInfo.receiveAmount = BigDecimal.ZERO
		totalInfo.value = mReceiveInfo

		mPriceInfo.sendPrice = BigDecimal.ZERO
		priceInfo.value = mPriceInfo
	}

	private fun getExchangeItem(coin: Coin): ExchangePairItem {
		val balance = adapterManager.adapters
			.firstOrNull { it.coin.code == coin.code }?.balance ?: BigDecimal.ZERO

		return ExchangePairItem(coin.code, coin.title, BigDecimal.ZERO, balance)
	}
	
	private fun getAvailableSendCoins(): List<ExchangePairItem> {
		// Send only available pair exchangeableCoins
		return exchangeableCoins
			.filter { coin -> coinPairsCodes
				.firstOrNull {
					when(this.exchangeState) {
						ExchangeSide.BID -> it.first == coin.code
						ExchangeSide.ASK -> it.second == coin.code
					}
				} != null
			}
			.map { getExchangeItem(it) }
	}

	private fun getAvailableReceiveCoins(baseCoinCode: String): List<ExchangePairItem> {
		// Receive available send coin pairs
		return exchangeableCoins
			.filter { coin ->
				coinPairsCodes.firstOrNull {
					when(this.exchangeState) {
						ExchangeSide.BID -> it.first.equals(baseCoinCode, true) &&
							it.second.equals(coin.code, true)
						
						ExchangeSide.ASK -> it.second.equals(baseCoinCode, true) &&
							it.first.equals(coin.code, true)
					}
				} != null
			}
			.map { getExchangeItem(it) }
	}
	
	private fun refreshPairs(state: LimitOrderViewState?, refreshSendCoins: Boolean = true) {
		if (refreshSendCoins) {
			mSendCoins = getAvailableSendCoins()
		}
		
		val sendCoin = state?.sendPair?.code ?: mSendCoins.first().code
		mReceiveCoins = getAvailableReceiveCoins(sendCoin)
	}
	
	private fun updateReceivePrice() {
		viewState.value?.sendAmount?.let { amount ->
			val receiveAmount = amount.multiply(mPriceInfo.sendPrice)
			mReceiveInfo.receiveAmount = receiveAmount

			totalInfo.value = mReceiveInfo
			
			exchangeEnabled.value = receiveAmount > BigDecimal.ZERO

			exchangePrice.value = relayer.calculateBasePrice(
				coinPairsCodes[currentPairPosition],
				if (exchangeState == ExchangeSide.BID) EOrderSide.BUY else EOrderSide.SELL
			)
		}
	}
	
	private fun placeOrder() {
		viewState.value?.sendAmount?.let { amount ->
			if (amount > BigDecimal.ZERO && mPriceInfo.sendPrice > BigDecimal.ZERO) {
				messageEvent.postValue(R.string.message_order_creating)
				
				relayer.createOrder(
					coinPairsCodes[currentPairPosition],
					if (exchangeState == ExchangeSide.BID) EOrderSide.SELL else EOrderSide.BUY,
					amount,
					mPriceInfo.sendPrice
				).subscribeUi(disposables, {}, {
					errorEvent.postValue(R.string.error_order_place)
					Logger.e(it)
				}, {
					messageEvent.postValue(R.string.message_order_created)
					initState(viewState.value?.sendPair, viewState.value?.receivePair)
				})
				
			} else {
				messageEvent.postValue(R.string.message_invalid_amount)
			}
		}
	}
	
	private fun showConfirm() {
		val pair = coinPairsCodes[currentPairPosition]
		
		val confirmInfo = ExchangeConfirmInfo(
			pair.first,
			pair.second,
			viewState.value?.sendAmount ?: BigDecimal.ZERO,
			mReceiveInfo.receiveAmount
		) { placeOrder() }

		confirmEvent.value = confirmInfo
	}
	
	//endregion
	
	//region Public
	
	fun onReceiveCoinPick(position: Int) {
		viewState.value?.receivePair = mReceiveCoins[position]
		updateReceivePrice()
	}
	
	fun onSendCoinPick(position: Int) {
		viewState.value?.sendPair = mSendCoins[position]
		refreshPairs(viewState.value, false)
		updateReceivePrice()
	}
	
	fun onSendAmountChange(amount: BigDecimal) {
		if (viewState.value?.sendAmount != amount) {
			viewState.value?.sendAmount = amount
			
			updateReceivePrice()
		}
	}
	
	fun onPriceChange(price: BigDecimal) {
		if (mPriceInfo.sendPrice != price) {
			mPriceInfo.sendPrice = price
			
			updateReceivePrice()
		}
	}
	
	fun onMaxClick() {
		val adapter = adapterManager.adapters.firstOrNull { it.coin.code == viewState.value?.sendPair?.code }
		if (adapter != null) {
		
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
		exchangeState = when(exchangeState) {
			ExchangeSide.BID -> ExchangeSide.ASK
			ExchangeSide.ASK -> ExchangeSide.BID
		}

		val currentReceive = mReceiveInfo.receiveAmount
		val currentPrice = if (mReceiveInfo.receiveAmount > BigDecimal.ZERO) {
			viewState.value?.sendAmount?.divide(mReceiveInfo.receiveAmount) ?: BigDecimal.ZERO
		} else {
			BigDecimal.ZERO
		}
		val currentSend = viewState.value?.sendAmount ?: BigDecimal.ZERO

		val newState = LimitOrderViewState(
			sendAmount = currentReceive,
			sendPair = viewState.value?.receivePair!!,
			receivePair = viewState.value?.sendPair!!
		)

		mReceiveInfo.receiveAmount = currentSend
		mPriceInfo.sendPrice = currentPrice

		refreshPairs(newState)

		viewState.value = newState
		totalInfo.value = mReceiveInfo
		priceInfo.value = mPriceInfo
	}
	
	//endregion
}