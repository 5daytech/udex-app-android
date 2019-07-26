package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.balance.recycler.BalanceViewHolder
import com.blocksdecoded.dex.presentation.balance.recycler.BalanceAdapter
import com.blocksdecoded.dex.ui.CoreFragment
import kotlinx.android.synthetic.main.fragment_balance.*

class BalanceFragment : CoreFragment(R.layout.fragment_balance),
        BalanceViewHolder.IWalletVHListener {

    private lateinit var adapter: BalanceAdapter
    private lateinit var viewModel: BalanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BalanceAdapter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BalanceViewModel::class.java)

        viewModel.balances.observe(this, Observer {
            adapter.setCoins(it)
        })

        viewModel.refreshing.observe(this, Observer {
            swipe_refresh.isRefreshing = it
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        balance_recycler.layoutManager = LinearLayoutManager(context)
        balance_recycler.adapter = adapter

        swipe_refresh.setOnRefreshListener { viewModel.refresh() }
    }

    //region ViewHolder

    override fun onClick(position: Int) = adapter.toggleViewHolder(position)

    override fun onSendClick(position: Int) = viewModel.onSendClick(position)
    override fun onReceiveClick(position: Int) = viewModel.onReceiveClick(position)
    override fun onTransactionsClick(position: Int) = viewModel.onTransactionsClick(position)
    override fun onConvertClick(position: Int) = viewModel.onConvertClick(position)

    //endregion

    companion object {
        fun newInstance() = BalanceFragment()
    }

}
