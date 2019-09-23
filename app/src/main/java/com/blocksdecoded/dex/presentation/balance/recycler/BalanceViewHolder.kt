package com.blocksdecoded.dex.presentation.balance.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinBalance
import com.blocksdecoded.dex.core.model.EConvertType.*
import com.blocksdecoded.dex.presentation.widgets.CoinIconView
import com.blocksdecoded.dex.utils.setVisible
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import java.math.BigDecimal

class BalanceViewHolder(
    view: View,
    private val listener: IWalletVHListener
): RecyclerView.ViewHolder(view) {

    private val mIcon: CoinIconView = itemView.findViewById(R.id.balance_icon)
    private val mSymbol: TextView = itemView.findViewById(R.id.balance_symbol)
    private val mTitle: TextView = itemView.findViewById(R.id.balance_title)
    private val mBalance: TextView = itemView.findViewById(R.id.balance_amount)
    private val mFiatBalance: TextView = itemView.findViewById(R.id.balance_fiat_amount)
    private val mTokenPrice: TextView = itemView.findViewById(R.id.balance_token_price)
    private val mButtonContainer: View = itemView.findViewById(R.id.balance_buttons_container)
    private val mCoinInfo: View = itemView.findViewById(R.id.balance_coin_info)
    
    private val mSendBtn: View = itemView.findViewById(R.id.balance_send)
    private val mReceiveBtn: View = itemView.findViewById(R.id.balance_receive)
    private val mTransactionsBtn: View = itemView.findViewById(R.id.balance_transactions)
    private val mConvertBtn: View = itemView.findViewById(R.id.balance_convert)
    private val mConvertType: TextView = itemView.findViewById(R.id.balance_convert_type)

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }

        mSendBtn.setOnClickListener { listener.onSendClick(adapterPosition) }
        mReceiveBtn.setOnClickListener { listener.onReceiveClick(adapterPosition) }
        mTransactionsBtn.setOnClickListener { listener.onTransactionsClick(adapterPosition) }
        mConvertBtn.setOnClickListener { listener.onConvertClick(adapterPosition) }
        mCoinInfo.setOnClickListener { listener.onInfoClick(adapterPosition) }
    }

    @SuppressLint("SetTextI18n")
    fun onBind(coinBalance: CoinBalance, expanded: Boolean) {
        mCoinInfo.visible = coinBalance.coin.shortInfoRes != null
        mIcon.bind(coinBalance.coin.code)
        mSymbol.text = coinBalance.coin.code
        mTitle.text = coinBalance.coin.title
        mBalance.text = "${coinBalance.balance.toDisplayFormat()} ${coinBalance.coin.code}"
        mFiatBalance.text = "$${coinBalance.fiatBalance.toFiatDisplayFormat()}"
        mTokenPrice.text = "$${coinBalance.pricePerToken.toFiatDisplayFormat()} per ${coinBalance.coin.code}"
        
        mButtonContainer.visible = expanded
    
        if (coinBalance.balance > BigDecimal.ZERO) {
            mSendBtn.isEnabled = true
            mSendBtn.alpha = 1f
            mConvertBtn.isEnabled = true
            mConvertBtn.alpha = 1f
        } else {
            mSendBtn.isEnabled = false
            mSendBtn.alpha = 0.4f
            mConvertBtn.isEnabled = false
            mConvertBtn.alpha = 0.4f
        }
        
        when(coinBalance.convertType) {
            NONE -> {
                mConvertBtn.visible = false
            }
            WRAP -> {
                mConvertBtn.visible = true
                mConvertType.setText(R.string.balance_wrap)
            }
            UNWRAP -> {
                mConvertBtn.visible = true
                mConvertType.setText(R.string.balance_unwrap)
            }
        }
    }

    fun bindPartial(expanded: Boolean) {
        itemView.isSelected = expanded
        mButtonContainer.setVisible(expanded, true)
    }

    interface IWalletVHListener {
        fun onClick(position: Int)

        fun onSendClick(position: Int)

        fun onReceiveClick(position: Int)

        fun onTransactionsClick(position: Int)

        fun onConvertClick(position: Int)

        fun onInfoClick(position: Int)
    }
}