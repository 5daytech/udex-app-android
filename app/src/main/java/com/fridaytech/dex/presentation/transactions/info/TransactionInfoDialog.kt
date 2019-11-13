package com.fridaytech.dex.presentation.transactions.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.dialogs.BaseBottomDialog
import com.fridaytech.dex.presentation.transactions.model.TransactionViewItem
import com.fridaytech.dex.utils.openTransactionUrl
import com.fridaytech.dex.utils.setTextColorRes
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.fridaytech.dex.utils.visible
import java.math.BigDecimal
import kotlinx.android.synthetic.main.dialog_transaction_info.*

class TransactionInfoDialog private constructor() :
    BaseBottomDialog(R.layout.dialog_transaction_info) {

    private lateinit var viewModel: TransactionInfoViewModel
    private lateinit var transactionItem: TransactionViewItem

    private val transactionDataObserver = Observer<TransactionViewItem> {
        val isPositive = it.coinValue > BigDecimal.ZERO

        transaction_info_hash?.update(it.transactionHash)

        transaction_info_coin_name.text = "${it.coin.title} "

        transaction_info_amount.text = "${if (isPositive) "+" else "-"} ${it.coinValue.abs().toDisplayFormat()} ${it.coin.code}"
        transaction_info_fiat_amount.text = "$${it.fiatValue?.abs()?.toFiatDisplayFormat()}"
        transaction_info_amount.setTextColorRes(if (isPositive) R.color.green else R.color.red)

        transaction_info_date.setDate(it.date)

        transaction_info_from.visible = it.incoming
        transaction_info_to.visible = !it.incoming
        transaction_info_from.setAddress(it.from)
        transaction_info_to.setAddress(it.to)

        transaction_info_hist_rate.setRate(it.coin, it.historicalRate)

        transaction_info_status.setStatus(it.status)

        transaction_info_fee.visible = it.fee != null && !it.incoming
        transaction_info_fee.setFiat(it.fiatFee)
    }

    private val fullTransactionInfoObserver = Observer<String> { transactionHash ->
        activity?.openTransactionUrl(transactionHash)
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

            viewModel.fullInfoEvent.observe(this, fullTransactionInfoObserver)

            viewModel.init(transactionItem)
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, transactionItem: TransactionViewItem) {
            val dialog = TransactionInfoDialog()

            dialog.transactionItem = transactionItem

            dialog.show(fragmentManager, "transaction_info")
        }
    }
}
