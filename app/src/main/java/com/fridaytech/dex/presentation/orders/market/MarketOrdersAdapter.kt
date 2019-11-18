package com.fridaytech.dex.presentation.orders.market

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.data.zrx.model.SimpleOrder
import com.fridaytech.dex.utils.inflate

class MarketOrdersAdapter(
    private val ordersListener: MarketOrderViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOrders = ArrayList<SimpleOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MarketOrderViewHolder(
            parent.inflate(R.layout.item_market_order),
            ordersListener
        )
    }

    override fun getItemCount(): Int = if (mOrders.size > 9) 9 else mOrders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MarketOrderViewHolder -> holder.onBind(mOrders[position])
        }
    }

    fun setOrders(orders: List<SimpleOrder>) {
        mOrders.clear()
        mOrders.addAll(orders)
        notifyDataSetChanged()
    }
}
