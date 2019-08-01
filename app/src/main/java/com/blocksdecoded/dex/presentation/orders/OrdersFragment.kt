package com.blocksdecoded.dex.presentation.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide.*
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.presentation.orders.recycler.OrderViewHolder
import com.blocksdecoded.dex.presentation.orders.recycler.OrdersAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment: CoreFragment(R.layout.fragment_orders), OrderViewHolder.Listener {

    private lateinit var adapter: OrdersAdapter
    private lateinit var viewModel: OrdersViewModel
    private var side = BUY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = OrdersAdapter(this)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null && activity != null) {
            viewModel = ViewModelProviders.of(activity!!)[OrdersViewModel::class.java]
            
            when(side) {
                BUY -> viewModel.uiBuyOrders
                SELL -> viewModel.uiSellOrders
                MY -> viewModel.uiMyOrders
            }.observe(this, Observer { adapter.setOrders(it) })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_recycler?.layoutManager = LinearLayoutManager(context)
        orders_recycler?.adapter = adapter
    }

    override fun onClick(position: Int) {
    
    }
    
    companion object {
        fun newInstance(orderSide: EOrderSide): Fragment = OrdersFragment().apply {
            side = orderSide
        }
    }
}