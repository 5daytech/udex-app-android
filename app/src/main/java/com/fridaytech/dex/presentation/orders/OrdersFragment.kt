package com.fridaytech.dex.presentation.orders

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridaytech.dex.R
import com.fridaytech.dex.core.ui.CoreFragment
import com.fridaytech.dex.presentation.common.ActionViewHolder
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.presentation.orders.model.EOrderSide.*
import com.fridaytech.dex.presentation.orders.recycler.OrderViewHolder
import com.fridaytech.dex.presentation.orders.recycler.OrdersAdapter
import com.fridaytech.dex.utils.getColorRes
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : CoreFragment(R.layout.fragment_orders),
    OrderViewHolder.Listener,
    ActionViewHolder.Listener {

    private lateinit var adapter: OrdersAdapter
    private lateinit var viewModel: OrdersViewModel
    private var side = BUY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionConfig = if (side == MY) {
            ActionViewHolder.ActionConfig(
                R.drawable.ic_cancel_circle,
                R.string.orders_cancel_all,
                context?.getColorRes(R.color.red) ?: Color.BLACK
            )
        } else {
            null
        }
        adapter = OrdersAdapter(this, this, actionConfig)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null && activity != null) {
            viewModel = ViewModelProviders.of(activity!!).get(OrdersViewModel::class.java)

            when (side) {
                BUY -> viewModel.buyOrders
                SELL -> viewModel.sellOrders
                MY -> viewModel.myOrders
            }.observe(this, Observer {
                orders_empty?.visible = it.isEmpty()
                adapter.setOrders(it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_recycler?.layoutManager = LinearLayoutManager(context)
        orders_recycler?.adapter = adapter
    }

    override fun onClick(position: Int) {
        viewModel.onOrderClick(position, side)
    }

    override fun onClick() {
        viewModel.onActionClick(side)
    }

    companion object {
        fun newInstance(orderSide: EOrderSide): Fragment = OrdersFragment().apply {
            side = orderSide
        }
    }
}
