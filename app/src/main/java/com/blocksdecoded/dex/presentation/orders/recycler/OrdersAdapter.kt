package com.blocksdecoded.dex.presentation.orders.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.UiOrder

class OrdersAdapter(
        private val listener: OrderViewHolder.Listener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOrders = ArrayList<UiOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false), listener)
    }

    override fun getItemCount(): Int = mOrders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is OrderViewHolder -> holder.onBind(mOrders[position])
        }
    }

    fun setOrders(orders: List<UiOrder>) {
        mOrders.clear()
        mOrders.addAll(orders)
        notifyDataSetChanged()
    }
}