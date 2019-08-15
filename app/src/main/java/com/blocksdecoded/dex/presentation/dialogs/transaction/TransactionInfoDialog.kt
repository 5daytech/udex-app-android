package com.blocksdecoded.dex.presentation.dialogs.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog

class TransactionInfoDialog private constructor()
	: BaseBottomDialog(R.layout.dialog_transaction_info) {
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
	}
	
	companion object {
		fun show(fragmentManager: FragmentManager, transactionInfo: TransactionInfo) {
			val fragment = TransactionInfoDialog()
			fragment.show(fragmentManager, "transaction_info")
		}
	}
	
}