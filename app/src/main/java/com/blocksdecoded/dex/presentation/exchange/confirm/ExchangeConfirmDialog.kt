package com.blocksdecoded.dex.presentation.exchange.confirm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import com.blocksdecoded.dex.presentation.widgets.click.setSingleClickListener
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import kotlinx.android.synthetic.main.dialog_confirm_exchange.*

class ExchangeConfirmDialog: BaseDialog(R.layout.dialog_confirm_exchange) {
	
	private lateinit var viewModel: ExchangeConfirmViewModel
	private lateinit var info: ExchangeConfirmInfo
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProviders.of(this).get(ExchangeConfirmViewModel::class.java)
		
		viewModel.init(info)
		
		viewModel.viewState.observe(this, Observer { refreshState(it) })

		viewModel.dismissEvent.observe(this, Observer { dismiss() })
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		exchange_confirm.setSingleClickListener { viewModel.onConfirmClick() }
	}
	
	private fun refreshState(state: ExchangeConfirmViewModel.ViewState) {
		exchange_confirm_from_coin?.bind(state.fromCoin.code)
		exchange_confirm_to_coin?.bind(state.toCoin.code)
		exchange_confirm_send_amount?.text = "${state.sendAmount.toDisplayFormat()} ${state.fromCoin.code}"
		exchange_confirm_receive_amount?.text = "${state.receiveAmount.toDisplayFormat()} ${state.toCoin.code}"
		exchange_confirm_price?.text = "${state.price.toDisplayFormat()} ${state.toCoin.code}"
	}

    companion object {
        fun open(fragmentManager: FragmentManager, info: ExchangeConfirmInfo) {
	        val fragment = ExchangeConfirmDialog()
	        
	        fragment.info = info
	        
	        fragment.show(fragmentManager, "exchange_confirm")
        }
    }

}