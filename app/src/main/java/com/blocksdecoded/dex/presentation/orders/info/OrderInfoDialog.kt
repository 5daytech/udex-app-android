package com.blocksdecoded.dex.presentation.orders.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.presentation.sent.SentDialog
import com.blocksdecoded.dex.presentation.orders.model.OrderInfoConfig
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toLongDisplayFormat
import kotlinx.android.synthetic.main.dialog_order_info.*

class OrderInfoDialog : BaseBottomDialog(R.layout.dialog_order_info) {
	
	private lateinit var viewModel: OrderInfoViewModel
	private var orderInfo: OrderInfoConfig? = null
	
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
		
		viewModel.successEvent.observe(this, Observer { hash ->
			fragmentManager?.let {
				SentDialog.open(it, hash)
			}
		})
		
		viewModel.messageEvent.observe(this, Observer {
			ToastHelper.showSuccessMessage(it)
		})

		viewModel.orderInfo.observe(this, Observer { order ->
			order_info_price.text = "${order.price.toLongDisplayFormat()} ${order.takerCoin.code}"

			order_info_amount.text = "${order.makerAmount.toDisplayFormat()} ${order.makerCoin.code}"
//			order_info_amount.text = if (order.side == EOrderSide.BUY) {
//				"${order.takerAmount.toDisplayFormat()} ${order.takerCoin.code}"
//			} else {
//				"${order.makerAmount.toDisplayFormat()} ${order.makerCoin.code}"
//			}

			order_info_receive_amount.text = "${order.takerAmount.toDisplayFormat()} ${order.takerCoin.code}"
//			order_info_receive_amount.text = if (order.side == EOrderSide.BUY) {
//				"${order.makerAmount.toDisplayFormat()} ${order.makerCoin.code}"
//			} else {
//				"${order.takerAmount.toDisplayFormat()} ${order.takerCoin.code}"
//			}

			order_info_filled_amount.text = "${order.filledAmount.toDisplayFormat()} ${order.takerCoin.code}"
			order_info_expire_date.text = order.expireDate
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		order_cancel.setOnClickListener { viewModel.onCancelClick() }
	}
	
	companion object {
		fun show(fragmentManager: FragmentManager, orderInfo: OrderInfoConfig) {
			val fragment = OrderInfoDialog()
			
			fragment.orderInfo = orderInfo
			
			fragment.show(fragmentManager, "order_info")
		}
	}
}