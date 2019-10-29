package com.blocksdecoded.dex.presentation.exchangehistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.SwipeableActivity
import com.blocksdecoded.dex.presentation.exchangehistory.recycler.ExchangeHistoryAdapter
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.utils.openTransactionUrl
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.activity_exchange_history.*

class ExchangeHistoryActivity : SwipeableActivity() {

    lateinit var adapter: ExchangeHistoryAdapter
    lateinit var viewModel: ExchangeHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_history)
        toolbar.bind(MainToolbar.getBackAction { finish() })

        adapter = ExchangeHistoryAdapter(listOf()) { viewModel.onTransactionClick(it) }

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
