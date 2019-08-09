package com.blocksdecoded.dex.presentation.dialogs.sent

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.clipboard.ClipboardManager
import com.blocksdecoded.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.dialog_transaction_sent.*

class SentDialog: DialogFragment() {
	
	var transactionHash: String? = null

	//TODO: Fetch from config
	private val etherscanUrl: String
		get() = "https://ropsten.etherscan.io/tx/$transactionHash"
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
		inflater.inflate(R.layout.dialog_transaction_sent, container, false)
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		dialog_sent_transaction_hash?.text = transactionHash.toString()
		
		dialog_sent_view_on?.setOnClickListener {
			CustomTabsIntent.Builder()
				.build()
				.launchUrl(activity, Uri.parse(etherscanUrl))

			dismiss()
		}

		dialog_sent_transaction_hash?.setOnClickListener {
			ClipboardManager.copyText(transactionHash.toString())
			ToastHelper.showSuccessMessage(R.string.message_copied, 1000)
		}
	}
	
	companion object {
		fun show(fragmentManager: FragmentManager, transactionHash: String) {
			val fragment = SentDialog()
			
			fragment.transactionHash = transactionHash
			
			fragment.show(fragmentManager, "transaction_sent")
		}
	}
}