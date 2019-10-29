package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.utils.inflate

class OrdersAdapter(
    private val listener: OrderViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOrders = ArrayList<UiOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(parent.inflate(R.layout.item_order), listener)
    }

    override fun getItemCount(): Int = mOrders.size

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
