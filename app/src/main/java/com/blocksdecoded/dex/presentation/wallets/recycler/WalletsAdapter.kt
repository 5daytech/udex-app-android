package com.blocksdecoded.dex.presentation.wallets.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinValue

class WalletsAdapter(
    private val listener: WalletViewHolder.IWalletVHListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mWallets = ArrayList<CoinValue>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        WalletViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wallet, parent, false), listener)

    override fun getItemCount(): Int = mWallets.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is WalletViewHolder -> holder.onBind(mWallets[position])
        }
    }
}