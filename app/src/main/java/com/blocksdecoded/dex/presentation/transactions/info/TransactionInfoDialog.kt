package com.blocksdecoded.dex.presentation.transactions.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import kotlinx.android.synthetic.main.dialog_transaction_info.*

class TransactionInfoDialog private constructor()
	: BaseBottomDialog(R.layout.dialog_transaction_info) {

    private lateinit var viewModel: TransactionInfoViewModel
    private lateinit var transactionInfo: TransactionInfo
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

        transaction_info_hash.update(transactionInfo.transactionRecord.transactionHash)
	}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(TransactionInfoViewModel::class.java)
        }
    }
	
	companion object {
		fun show(fragmentManager: FragmentManager, transactionInfo: TransactionInfo) {
			val fragment = TransactionInfoDialog()

            fragment.transactionInfo = transactionInfo

			fragment.show(fragmentManager, "transaction_info")
		}
	}
	
}