package com.blocksdecoded.dex.presentation.balance.recycler

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinValue
import com.blocksdecoded.dex.core.model.EConvertType.*
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.utils.setVisible
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import java.math.BigDecimal

class BalanceViewHolder(
    view: View,
    private val listener: IWalletVHListener
): RecyclerView.ViewHolder(view) {

    private val mIcon: CoinIconImage = itemView.findViewById(R.id.balance_icon)
    private val mSymbol: TextView = itemView.findViewById(R.id.balance_symbol)
    private val mTitle: TextView = itemView.findViewById(R.id.balance_title)
    private val mBalance: TextView = itemView.findViewById(R.id.balance_amount)
    private val mFiatBalance: TextView = itemView.findViewById(R.id.balance_fiat_amount)
    private val mButtonContainer: View = itemView.findViewById(R.id.balance_buttons_container)
    
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
    }

    @SuppressLint("SetTextI18n")
    fun onBind(coinValue: CoinValue, expanded: Boolean) {
        mIcon.bind(coinValue.coin.code)
        mSymbol.text = coinValue.coin.code
        mTitle.text = coinValue.coin.title
        mBalance.text = "${coinValue.balance.toDisplayFormat()} ${coinValue.coin.code}"
        mFiatBalance.text = "$${coinValue.fiatBalance.toFiatDisplayFormat()}"
        mButtonContainer.visible = expanded
    
        if (coinValue.balance <= BigDecimal.ZERO) {
            mSendBtn.isEnabled = false
            mSendBtn.alpha = 0.4f
            mConvertBtn.isEnabled = false
            mConvertBtn.alpha = 0.4f
        } else {
            mSendBtn.isEnabled = true
            mSendBtn.alpha = 1f
            mConvertBtn.isEnabled = true
            mConvertBtn.alpha = 1f
        }
        
        when(coinValue.convertType) {
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
    }
}