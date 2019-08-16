package com.blocksdecoded.dex.presentation.exchange.confirm

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import java.math.BigDecimal

class ExchangeConfirmViewModel: CoreViewModel() {
	
	private var confirmInfo: ExchangeConfirmInfo? = null

	val viewState = MutableLiveData<ViewState>()
	val dismissEvent = SingleLiveEvent<Unit>()

	fun init(info: ExchangeConfirmInfo) {
		confirmInfo = info
		
		val state = ViewState(
			CoinManager.getCoin(info.sendCoin),
			CoinManager.getCoin(info.receiveCoin),
			info.sendAmount,
			info.receiveAmount,
			info.receiveAmount.divide(info.sendAmount)
		)
		
		viewState.value = state
	}
	
	fun onConfirmClick() {
		confirmInfo?.onConfirm?.invoke()
		dismissEvent.call()
	}
	
	data class ViewState(
		val fromCoin: Coin,
		val toCoin: Coin,
		val sendAmount: BigDecimal,
		val receiveAmount: BigDecimal,
		val price: BigDecimal
	)
}