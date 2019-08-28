package com.blocksdecoded.dex.presentation.transactions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.transactions.recycler.TransactionViewHolder
import com.blocksdecoded.dex.presentation.transactions.recycler.TransactionsAdapter
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.transactions.info.TransactionInfoDialog
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.fragment_transactions.*

class TransactionsFragment : CoreFragment(R.layout.fragment_transactions),
        TransactionViewHolder.OnClickListener {
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var adapter: TransactionsAdapter
    private var coinCode: String? = null

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TransactionsAdapter(this, coinCode ?: "")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransactionsViewModel::class.java)
        viewModel.init(coinCode)

        viewModel.errorEvent.observe(this, Observer { ToastHelper.showErrorMessage(it) })
        viewModel.messageEvent.observe(this, Observer { ToastHelper.showSuccessMessage(it) })
        viewModel.finishEvent.observe(this, Observer { activity?.finish() })

        viewModel.transactions.observe(this, Observer { adapter.setTransactions(it) })
        viewModel.showTransactionInfoEvent.observe(this, Observer {
            TransactionInfoDialog.show(childFragmentManager, it)
        })

        viewModel.coinName.observe(this, Observer {
            if (it != null) toolbar?.title = "$it Transactions"
        })
        viewModel.balance.observe(this, Observer {
            if (it != null) transactions_total_balance.update(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactions_recycler?.adapter = adapter
        transactions_recycler?.layoutManager = LinearLayoutManager(context)

        toolbar?.bind(MainToolbar.ToolbarState.BACK) { viewModel.onBackClick() }
    }

    //endregion

    override fun onClick(position: Int) {
        viewModel.onTransactionClick(position)
    }

    companion object {
        fun newInstance(coinCode: String) = TransactionsFragment().apply {
            this.coinCode = coinCode
        }
    }
}
