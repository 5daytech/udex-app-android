package com.fridaytech.dex.presentation.coinmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.SwipeableActivity
import com.fridaytech.dex.presentation.widgets.MainToolbar
import com.fridaytech.dex.presentation.widgets.SpaceItemDecoration
import com.fridaytech.dex.utils.dpToPx
import kotlinx.android.synthetic.main.activity_coin_manager.*

class CoinManagerActivity : SwipeableActivity(),
    CoinManagerAdapter.Listener,
    IDragListener {

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
        itemTouchHelper = ItemTouchHelper(
            CoinDragHelperCallback(
                adapter
            )
        )
        itemTouchHelper?.attachToRecyclerView(coin_manager_recycler)
        coin_manager_recycler.addItemDecoration(SpaceItemDecoration(bottom = dpToPx(48)))

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
