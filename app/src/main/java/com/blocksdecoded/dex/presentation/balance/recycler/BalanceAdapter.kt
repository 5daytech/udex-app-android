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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BalanceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_balance, parent, false), listener)

    override fun getItemCount(): Int = mBalances.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is BalanceViewHolder -> holder.onBind(mBalances[position])
        }
    }

    fun setCoins(coins: List<CoinValue>) {
        mBalances.clear()
        mBalances.addAll(coins)
        notifyDataSetChanged()
    }
}