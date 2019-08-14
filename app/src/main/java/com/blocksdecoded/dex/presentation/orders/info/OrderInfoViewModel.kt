package com.blocksdecoded.dex.presentation.orders.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.OrderInfo
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.subscribeUi
import com.blocksdecoded.zrxkit.model.SignedOrder

class OrderInfoViewModel : CoreViewModel() {
	private val relayerAdapter = App.relayerAdapterManager.getMainAdapter()
	private var order: OrderInfo? = null
	
	val orderInfo = MutableLiveData<UiOrder>()
	val dismissEvent = SingleLiveEvent<Unit>()
	val errorEvent = SingleLiveEvent<Int>()
	val messageEvent = SingleLiveEvent<Int>()
	val successEvent = SingleLiveEvent<String>()
	
	fun init(orderInfo: OrderInfo?) {
		this.order = orderInfo
		
		order?.let {
			this.orderInfo.value = UiOrder.fromOrder(it.order, it.side, isMine = true)
		} ?: dismissEvent.call()
	}
	
	fun onCancelClick() {
		order?.let {
			messageEvent.postValue(R.string.message_cancel_started)
			relayerAdapter.cancelOrder(it.order)
				.subscribeUi(disposables, {
					successEvent.postValue(it)
					dismissEvent.call()
				}, {
					errorEvent.postValue(R.string.error_cancel_order)
				})
		} ?: dismissEvent.call()
	}
}