package com.fridaytech.dex.presentation.balance.recycler

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.core.model.BalanceState.*
import com.fridaytech.dex.core.model.CoinBalance
import com.fridaytech.dex.core.model.EConvertType.*
import com.fridaytech.dex.utils.setVisible
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.fridaytech.dex.utils.visible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_balance.*
import java.math.BigDecimal

class BalanceViewHolder(
    override val containerView: View,
    private val listener: Listener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }

        balance_send.setOnClickListener { listener.onSendClick(adapterPosition) }
        balance_receive.setOnClickListener { listener.onReceiveClick(adapterPosition) }
        balance_transactions.setOnClickListener { listener.onTransactionsClick(adapterPosition) }
        balance_convert.setOnClickListener { listener.onConvertClick(adapterPosition) }
        balance_coin_info.setOnClickListener { listener.onInfoClick(adapterPosition) }
        balance_rate_stats.setOnClickListener { listener.onRateStatsClick(adapterPosition) }
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

        val isPositive = coinBalance.balance > BigDecimal.ZERO
        balance_send.isEnabled = isPositive
        balance_convert.isEnabled = isPositive
        balance_send.alpha = if (isPositive) 1f else 0.4f
        balance_convert.alpha = if (isPositive) 1f else 0.4f

        when (coinBalance.convertType) {
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

        balance_sync_progress.visible = false
        balance_icon.visible = false
        balance_sync_error.visible = false

        when (coinBalance.state) {
            SYNCED -> {
                balance_icon.visible = true
            }
            SYNCING -> {
                balance_sync_progress.visible = true
            }
            FAILED -> {
                balance_sync_error.visible = true
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

        fun onRateStatsClick(position: Int)

        fun onInfoClick(position: Int)
    }
}
