package com.fridaytech.dex.presentation.exchangehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.SwipeableActivity
import com.fridaytech.dex.presentation.exchangehistory.recycler.ExchangeHistoryAdapter
import com.fridaytech.dex.presentation.widgets.MainToolbar
import com.fridaytech.dex.utils.openTransactionUrl
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.activity_exchange_history.*

class ExchangeHistoryActivity : SwipeableActivity() {

    lateinit var adapter: ExchangeHistoryAdapter
    lateinit var viewModel: ExchangeHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_history)
        toolbar.bind(MainToolbar.getBackAction { finish() })

        adapter =
            ExchangeHistoryAdapter(listOf()) {
                viewModel.onTransactionClick(it)
            }

        exchange_history_recycler?.layoutManager = LinearLayoutManager(this)
        exchange_history_recycler?.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(ExchangeHistoryViewModel::class.java)

        viewModel.trades.observe(this, Observer {
            adapter.setTrades(it)
        })

        viewModel.emptyTradesVisible.observe(this, Observer {
            empty_view?.visible = it
        })

        viewModel.openTransactionInfoEvent.observe(this, Observer {
            openTransactionUrl(it)
        })
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ExchangeHistoryActivity::class.java)

            context.startActivity(intent)
        }
    }
}
