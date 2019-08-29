package com.blocksdecoded.dex.presentation.transactions.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.synthetic.main.dialog_transaction_info.*
import java.math.BigDecimal

class TransactionInfoDialog private constructor()
	: BaseBottomDialog(R.layout.dialog_transaction_info) {

    private lateinit var viewModel: TransactionInfoViewModel
    private lateinit var transactionInfo: TransactionInfo

    private val transactionDataObserver = Observer<TransactionViewData> {
        val isPositive = it.coinValue > BigDecimal.ZERO

        transaction_info_hash?.update(it.transactionHash)

        transaction_info_coin_icon.bind(it.coin.code)

        transaction_info_amount.text = "${if (isPositive) "+" else "-"} ${it.coinValue.abs().toDisplayFormat()} ${it.coin.code}"
        transaction_info_fiat_amount.text = "$${it.fiatValue?.abs()?.toFiatDisplayFormat()}"
        transaction_info_amount.setTextColorRes(if (isPositive) R.color.green else R.color.red)
    }
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

        transaction_info_full?.setOnClickListener {
            viewModel.onFullInfoClicked()
        }
	}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(TransactionInfoViewModel::class.java)

            viewModel.transactionView.observe(this, transactionDataObserver)

            viewModel.init(transactionInfo)
        }
    }
	
	companion object {
		fun show(fragmentManager: FragmentManager, transactionInfo: TransactionInfo) {
			val dialog = TransactionInfoDialog()

            dialog.transactionInfo = transactionInfo

			dialog.show(fragmentManager, "transaction_info")
		}
	}
	
}