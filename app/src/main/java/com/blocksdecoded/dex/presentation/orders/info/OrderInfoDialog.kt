package com.blocksdecoded.dex.presentation.orders.info

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.presentation.dialogs.sent.SentDialog
import com.blocksdecoded.dex.presentation.orders.model.OrderInfo
import com.blocksdecoded.dex.utils.ui.ToastHelper

class OrderInfoDialog : BaseBottomDialog(R.layout.dialog_order_info) {
	
	private lateinit var viewModel: OrderInfoViewModel
	private var orderInfo: OrderInfo? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		viewModel = ViewModelProviders.of(this).get(OrderInfoViewModel::class.java)
		viewModel.init(orderInfo)
		
		viewModel.dismissEvent.observe(this, Observer {
			dismiss()
		})
		
		viewModel.errorEvent.observe(this, Observer {
			ToastHelper.showErrorMessage(it)
		})
		
		viewModel.successEvent.observe(this, Observer {
			SentDialog.show(childFragmentManager, it)
		})
		
		viewModel.orderInfo.observe(this, Observer {
		
		})
	}
	
	companion object {
		fun show(fragmentManager: FragmentManager, orderInfo: OrderInfo) {
			val fragment = OrderInfoDialog()
			
			fragment.orderInfo = orderInfo
			
			fragment.show(fragmentManager, "order_info")
		}
	}
}