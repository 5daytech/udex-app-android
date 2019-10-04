package com.blocksdecoded.dex.presentation.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import com.blocksdecoded.dex.core.manager.clipboard.ClipboardManager
import com.blocksdecoded.dex.utils.openTransactionUrl
import com.blocksdecoded.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.dialog_transaction_sent.*

class TransactionSentDialog private constructor()
	: BaseDialog(R.layout.dialog_transaction_sent) {
	
	var transactionHash: String? = null
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		dialog_sent_transaction_hash?.update(transactionHash.toString())
		
		dialog_sent_view_on?.setOnClickListener {
			transactionHash?.let {
				activity?.openTransactionUrl(it)
			}

			dismiss()
		}

		dialog_sent_transaction_hash?.setOnClickListener {
			ClipboardManager.copyText(transactionHash.toString())
			ToastHelper.showSuccessMessage(R.string.message_copied, 1000)
		}
	}
	
	companion object {
		fun open(fragmentManager: FragmentManager, transactionHash: String) {
			val fragment = TransactionSentDialog()
			
			fragment.transactionHash = transactionHash
			
			fragment.show(fragmentManager, "transaction_sent")
		}
	}
}