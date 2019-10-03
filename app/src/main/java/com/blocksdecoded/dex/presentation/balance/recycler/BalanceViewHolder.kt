package com.blocksdecoded.dex.presentation.balance.recycler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.CoinBalance
import com.blocksdecoded.dex.core.model.EConvertType.*
import com.blocksdecoded.dex.utils.setVisible
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_balance.*
import java.math.BigDecimal

class BalanceViewHolder(
    override val containerView: View,
    private val listener: Listener
): RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }

        balance_send.setOnClickListener { listener.onSendClick(adapterPosition) }
        balance_receive.setOnClickListener { listener.onReceiveClick(adapterPosition) }
        balance_transactions.setOnClickListener { listener.onTransactionsClick(adapterPosition) }
        balance_convert.setOnClickListener { listener.onConvertClick(adapterPosition) }
        balance_coin_info.setOnClickListener { listener.onInfoClick(adapterPosition) }
    }

    @SuppressLint("SetTextI18n")
    fun onBind(coinBalance: CoinBalance, expanded: Boolean) {
        balance_coin_info.visible = coinBalance.coin.shortInfoRes != null
        balance_icon.bind(coinBalance.coin.code)
        balance_symbol.text = coinBalance.coin.code
        balance_title.text = coinBalance.coin.title
        balance_amount.text = "${coinBalance.balance.toDisplayFormat()} ${coinBalance.coin.code}"
        balance_fiat_amount.text = "$${coinBalance.fiatBalance.toFiatDisplayFormat()}"
        balance_token_price.text = "$${coinBalance.pricePerToken.toFiatDisplayFormat()} per ${coinBalance.coin.code}"
        
        balance_buttons_container.visible = expanded
    
        if (coinBalance.balance > BigDecimal.ZERO) {
            balance_send.isEnabled = true
            balance_send.alpha = 1f
            balance_convert.isEnabled = true
            balance_convert.alpha = 1f
        } else {
            balance_send.isEnabled = false
            balance_send.alpha = 0.4f
            balance_convert.isEnabled = false
            balance_convert.alpha = 0.4f
        }
        
        when(coinBalance.convertType) {
            NONE -> {
                balance_convert.visible = false
            }
            WRAP -> {
                balance_convert.visible = true
                balance_convert_type.setText(R.string.action_wrap)
            }
            UNWRAP -> {
                balance_convert.visible = true
                balance_convert_type.setText(R.string.action_unwrap)
            }
        }
    }

    fun bindPartial(expanded: Boolean) {
        itemView.isSelected = expanded
        balance_buttons_container.setVisible(expanded, true)
    }

    interface Listener {
        fun onClick(position: Int)

        fun onSendClick(position: Int)

        fun onReceiveClick(position: Int)

        fun onTransactionsClick(position: Int)

        fun onConvertClick(position: Int)

        fun onInfoClick(position: Int)
    }
}