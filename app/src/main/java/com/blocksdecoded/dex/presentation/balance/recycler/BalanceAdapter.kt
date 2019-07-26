package com.blocksdecoded.dex.presentation.balance.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinValue

class BalanceAdapter(
    private val listener: BalanceViewHolder.IWalletVHListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mBalances = ArrayList<CoinValue>()
    private var mExpandedViewPosition: Int? = null

    override fun getItemCount(): Int = mBalances.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BalanceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_balance, parent, false), listener)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when(holder) {
            is BalanceViewHolder -> {
                if (payloads.isEmpty()) {
                    holder.onBind(mBalances[position], mExpandedViewPosition == position)
                } else if (payloads.any { it is Boolean }) {
                    holder.bindPartial(mExpandedViewPosition == position)
                }
            }
        }
    }

    fun setCoins(coins: List<CoinValue>) {
        mBalances.clear()
        mBalances.addAll(coins)
        notifyDataSetChanged()
    }

    fun toggleViewHolder(position: Int) {
        mExpandedViewPosition?.let {
            notifyItemChanged(it, false)
        }

        if (mExpandedViewPosition != position) {
            notifyItemChanged(position, true)
        }

        mExpandedViewPosition = if (mExpandedViewPosition == position) null else position
    }
}