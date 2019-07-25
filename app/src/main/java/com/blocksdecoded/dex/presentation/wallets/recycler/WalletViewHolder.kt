package com.blocksdecoded.dex.presentation.wallets.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.core.model.CoinValue

class WalletViewHolder(
    view: View,
    private val listener: IWalletVHListener
): RecyclerView.ViewHolder(view) {

    fun onBind(coinValue: CoinValue) {

    }

    interface IWalletVHListener {
        fun onClick(position: Int)

        fun onSendClick(position: Int)

        fun onReceiveClick(position: Int)
    }
}