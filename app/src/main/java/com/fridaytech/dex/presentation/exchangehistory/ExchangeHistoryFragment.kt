package com.fridaytech.dex.presentation.exchangehistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreFragment
import com.fridaytech.dex.presentation.exchangehistory.recycler.ExchangeHistoryAdapter
import com.fridaytech.dex.utils.openTransactionUrl
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.activity_exchange_history.*

class ExchangeHistoryFragment : CoreFragment(R.layout.activity_exchange_history) {

    lateinit var adapter: ExchangeHistoryAdapter
    lateinit var viewModel: ExchangeHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ExchangeHistoryAdapter(listOf()) {
            viewModel.onTransactionClick(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this)
            .get(ExchangeHistoryViewModel::class.java)

        viewModel.trades.observe(this, Observer {
            adapter.setTrades(it)
        })

        viewModel.emptyTradesVisible.observe(this, Observer {
            empty_view?.visible = it
        })

        viewModel.openTransactionInfoEvent.observe(this, Observer {
            activity?.openTransactionUrl(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exchange_history_recycler?.layoutManager = LinearLayoutManager(activity)
        exchange_history_recycler?.adapter = adapter
    }

    companion object {
        fun newInstance(): Fragment {
            return ExchangeHistoryFragment()
        }
    }
}
