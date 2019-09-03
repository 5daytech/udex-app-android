package com.blocksdecoded.dex.presentation.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.history.recycler.TradeHistoryAdapter
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : CoreActivity() {

    lateinit var adapter: TradeHistoryAdapter
    lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        toolbar.bind(MainToolbar.ToolbarState.BACK) { finish() }

        adapter = TradeHistoryAdapter(listOf())
        trade_history_recycler?.layoutManager = LinearLayoutManager(this)
        trade_history_recycler?.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        viewModel.trades.observe(this, Observer {
            adapter.setTrades(it)
        })
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HistoryActivity::class.java)

            context.startActivity(intent)
        }
    }
}