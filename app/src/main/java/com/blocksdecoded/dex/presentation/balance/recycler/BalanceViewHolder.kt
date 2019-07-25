package com.blocksdecoded.dex.presentation.balance.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinValue
import com.blocksdecoded.dex.core.utils.toDisplayFormat

class BalanceViewHolder(
    view: View,
    private val listener: IWalletVHListener
): RecyclerView.ViewHolder(view) {

    private val mIcon: ImageView = itemView.findViewById(R.id.balance_icon)
    private val mSymbol: TextView = itemView.findViewById(R.id.balance_symbol)
    private val mTitle: TextView = itemView.findViewById(R.id.balance_title)
    private val mBalance: TextView = itemView.findViewById(R.id.balance_amount)

    @SuppressLint("SetTextI18n")
    fun onBind(coinValue: CoinValue) {
        mSymbol.text = coinValue.coin.code
        mTitle.text = coinValue.coin.title
        mBalance.text = "${coinValue.value.toDisplayFormat()} ${coinValue.coin.code}"
    }

    interface IWalletVHListener {
        fun onClick(position: Int)

        fun onSendClick(position: Int)

        fun onReceiveClick(position: Int)
    }
}