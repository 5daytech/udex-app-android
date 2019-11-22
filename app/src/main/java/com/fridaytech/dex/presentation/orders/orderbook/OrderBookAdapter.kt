package com.fridaytech.dex.presentation.orders.orderbook

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.utils.inflate

class OrderBookAdapter(
    private val ordersListener: OrderBookViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOrders = ArrayList<SimpleOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderBookViewHolder(
            parent.inflate(R.layout.item_order_book),
            ordersListener
        )
    }

    override fun getItemCount(): Int = if (mOrders.size > 9) 9 else mOrders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderBookViewHolder -> holder.onBind(mOrders[position])
        }
    }

    fun setOrders(orders: List<SimpleOrder>) {
        mOrders.clear()
        mOrders.addAll(orders)
        notifyDataSetChanged()
    }
}
