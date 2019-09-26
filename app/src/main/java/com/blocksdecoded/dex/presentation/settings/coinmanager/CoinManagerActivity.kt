package com.blocksdecoded.dex.presentation.settings.coinmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import kotlinx.android.synthetic.main.activity_coin_manager.*

class CoinManagerActivity : CoreActivity(), CoinManagerAdapter.Listener, IDragListener {

    private lateinit var viewModel: CoinManagerViewModel
    private lateinit var adapter: CoinManagerAdapter
    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_manager)

        viewModel = ViewModelProviders.of(this).get(CoinManagerViewModel::class.java)
        viewModel.init()

        adapter = CoinManagerAdapter(this, this)
        adapter.viewModel = viewModel
        coin_manager_recycler?.adapter = adapter
        coin_manager_recycler?.layoutManager = LinearLayoutManager(this)
        itemTouchHelper = ItemTouchHelper(CoinDragHelperCallback(adapter))
        itemTouchHelper?.attachToRecyclerView(coin_manager_recycler)

        toolbar?.bind(MainToolbar.getBackAction { viewModel.onBackPress() })

        coin_manager_save?.setOnClickListener { viewModel.onSaveClick() }

        viewModel.finishEvent.observe(this, Observer { finish() })

        viewModel.syncCoinsEvent.observe(this, Observer { adapter.notifyDataSetChanged() })
    }

    override fun onEnabledItemClick(position: Int) {
        viewModel.disableCoin(position)
    }

    override fun onDisabledItemClick(position: Int) {
        viewModel.enableCoin(position)
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CoinManagerActivity::class.java)
            context.startActivity(intent)
        }
    }
}