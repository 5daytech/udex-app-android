package com.fridaytech.dex.presentation.balance.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.core.model.CoinBalance
import com.fridaytech.dex.presentation.common.ActionViewHolder
import com.fridaytech.dex.utils.getAttr
import com.fridaytech.dex.utils.inflate

class BalanceAdapter(
    private val walletListener: BalanceViewHolder.Listener,
    private val manageCoinsListener: ActionViewHolder.Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_WALLET = 1
    private val TYPE_MANAGE_COINS = 2

    private val mBalances = ArrayList<CoinBalance>()
    private var mExpandedViewPosition: Int? = null

    override fun getItemCount(): Int = mBalances.size + 1

    override fun getItemViewType(position: Int): Int = when {
        position in 0 until mBalances.size -> TYPE_WALLET
        else -> TYPE_MANAGE_COINS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_WALLET -> BalanceViewHolder(
                parent.inflate(
                    R.layout.item_balance
                ), walletListener
            )
            else -> {
                val tint = parent.context.theme.getAttr(R.attr.AccentTextColor) ?: 0

                ActionViewHolder(
                    parent.inflate(R.layout.item_action),
                    ActionViewHolder.ActionConfig(
                        R.drawable.ic_manage_coins,
                        R.string.action_add_coin,
                        tint
                    ),
                    manageCoinsListener
                )
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (holder) {
            is BalanceViewHolder -> {
                if (payloads.isEmpty()) {
                    holder.onBind(mBalances[position], mExpandedViewPosition == position)
                } else if (payloads.any { it is Boolean }) {
                    holder.bindPartial(mExpandedViewPosition == position)
                }
            }
        }
    }

    fun setCoins(coins: List<CoinBalance>) {
        val diffResult = DiffUtil.calculateDiff(
            BalanceDiffCallback(
                mBalances,
                coins
            )
        )
        mBalances.clear()
        mBalances.addAll(coins)
        diffResult.dispatchUpdatesTo(this)
    }

    fun expandViewHolder(position: Int) {
        if (itemCount < position) return

        mExpandedViewPosition?.let {
            notifyItemChanged(it, false)
        }

        if (mExpandedViewPosition != position) {
            notifyItemChanged(position, true)
            mExpandedViewPosition = position
        }
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
