package com.blocksdecoded.dex.presentation.balance

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.balance.recycler.BalanceViewHolder
import com.blocksdecoded.dex.presentation.balance.recycler.BalanceAdapter
import com.blocksdecoded.dex.presentation.receive.ReceiveDialog
import com.blocksdecoded.dex.presentation.send.SendDialog
import com.blocksdecoded.dex.presentation.transactions.TransactionsActivity
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.convert.ConvertDialog
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.fragment_balance.*
import kotlinx.android.synthetic.main.view_top_up_account.*

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

        viewModel.totalBalance.observe(this, Observer {
            balance_total?.update(it)
        })

        viewModel.refreshing.observe(this, Observer {})

        viewModel.openReceiveDialog.observe(this, Observer { coinCode ->
            activity?.let {
                ReceiveDialog.open(it.supportFragmentManager, coinCode)
            }
        })

        viewModel.openSendDialog.observe(this, Observer { coinCode ->
            activity?.let {
                SendDialog.open(it.supportFragmentManager, coinCode)
            }
        })

        viewModel.openConvertDialog.observe(this, Observer { config ->
            activity?.let {
                ConvertDialog.open(it.supportFragmentManager, config)
            }
        })

        viewModel.openTransactions.observe(this, Observer { coinCode ->
            activity?.let {
                TransactionsActivity.start(it, coinCode)
            }
        })

        viewModel.totalBalanceVisible.observe(this, Observer {
            balance_total?.visible = it
        })

        viewModel.topUpVisible.observe(this, Observer {
            top_up_container?.visible = it
        })

        viewModel.openCoinInfo.observe(this, Observer {
            CoinInfoDialog.show(childFragmentManager, it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balance_recycler?.layoutManager = object: LinearLayoutManager(context) {
            override fun supportsPredictiveItemAnimations(): Boolean = false
        }

        balance_recycler?.adapter = adapter
        (balance_recycler?.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        
        swipe_refresh?.setOnRefreshListener {
            viewModel.refresh()
            swipe_refresh?.isRefreshing = false
        }

        top_up_add_coins?.setOnClickListener { viewModel.onAddCoinsClick() }
    }

    //region ViewHolder

    override fun onClick(position: Int) = adapter.toggleViewHolder(position)

    override fun onSendClick(position: Int) = viewModel.onSendClick(position)
    override fun onReceiveClick(position: Int) = viewModel.onReceiveClick(position)
    override fun onTransactionsClick(position: Int) = viewModel.onTransactionsClick(position)
    override fun onConvertClick(position: Int) = viewModel.onConvertClick(position)
    override fun onInfoClick(position: Int) = viewModel.onInfoClick(position)
    //endregion

    companion object {
        fun newInstance() = BalanceFragment()
    }

}
