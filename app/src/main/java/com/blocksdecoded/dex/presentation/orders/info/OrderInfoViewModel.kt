package com.blocksdecoded.dex.presentation.orders.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.App.Companion.coinManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.core.zrx.IRelayerAdapter
import com.blocksdecoded.dex.presentation.orders.model.OrderInfoConfig
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.uiSubscribe

class OrderInfoViewModel : CoreViewModel() {
	private val coinManager = App.coinManager
	private val ratesConverter = App.ratesConverter
	private val relayerAdapter: IRelayerAdapter?
		get() = App.relayerAdapterManager.mainRelayer
	private var order: OrderInfoConfig? = null
	
	val orderInfo = MutableLiveData<UiOrder>()
	val dismissEvent = SingleLiveEvent<Unit>()
	val successEvent = SingleLiveEvent<String>()
	
	fun init(orderInfo: OrderInfoConfig?) {
		this.order = orderInfo
		
		order?.let {
			this.orderInfo.value = UiOrder.fromOrder(
				coinManager,
				ratesConverter,
				it.order,
				it.side,
				orderInfo = it.info,
				isMine = true
			)
		} ?: dismissEvent.call()
	}
	
	fun onCancelClick() {
		order?.let {
			messageEvent.postValue(R.string.message_cancel_started)
			relayerAdapter?.cancelOrder(it.order)
				?.uiSubscribe(disposables, {
					successEvent.postValue(it)
					dismissEvent.call()
				}, {
					errorEvent.postValue(R.string.error_cancel_order)
				})
		} ?: dismissEvent.call()
	}
}