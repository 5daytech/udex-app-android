package com.blocksdecoded.dex.presentation.exchange.confirm

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.model.FeeInfo
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class ExchangeConfirmViewModel: CoreViewModel() {

	private val adapterManager = App.adapterManager
	private val coinManager = App.coinManager
	private var confirmInfo: ExchangeConfirmInfo? = null

	val viewState = MutableLiveData<ViewState>()
	val feeInfo = MutableLiveData<FeeInfo>()
	val dismissEvent = SingleLiveEvent<Unit>()

	fun init(info: ExchangeConfirmInfo) {
		confirmInfo = info

		val sendAdapter = adapterManager.adapters.firstOrNull { it.coin.code == info.sendCoin }
		val sendCoin = coinManager.getCoin(info.sendCoin)
		
		val state = ViewState(
			sendCoin,
			coinManager.getCoin(info.receiveCoin),
			info.sendAmount,
			info.receiveAmount,
			BigDecimal.ZERO
		)
		
		viewState.value = state

		feeInfo.value = FeeInfo(
			sendAdapter?.feeCoinCode ?: "",
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			0
		)

		App.zrxKitManager.zrxKit()
			.marketBuyEstimatedPrice
			.uiSubscribe(disposables, {
				val updatedFee = feeInfo.value?.copy(amount = it)
				feeInfo.value = updatedFee
			})
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