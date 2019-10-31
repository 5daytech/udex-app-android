package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.common.ActionViewHolder
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.inflate

class OrdersAdapter(
    private val ordersListener: OrderViewHolder.Listener,
    private val actionListener: ActionViewHolder.Listener,
    private val actionConfig: ActionViewHolder.ActionConfig? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOrders = ArrayList<UiOrder>()

    private val TYPE_ACTION = 1
    private val TYPE_ORDER = 2

    override fun getItemViewType(position: Int): Int = if (position == mOrders.size && actionConfig != null) {
        TYPE_ACTION
    } else {
        TYPE_ORDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ACTION -> ActionViewHolder(parent.inflate(R.layout.item_action), actionConfig, actionListener)
            else -> OrderViewHolder(parent.inflate(R.layout.item_order), ordersListener)
        }
    }

    override fun getItemCount(): Int = if (actionConfig != null && mOrders.isNotEmpty()) {
        mOrders.size + 1
    } else {
        mOrders.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderViewHolder -> holder.onBind(mOrders[position])
        }
    }

    fun setOrders(orders: List<UiOrder>) {
        mOrders.clear()
        mOrders.addAll(orders)
        notifyDataSetChanged()
    }
}
