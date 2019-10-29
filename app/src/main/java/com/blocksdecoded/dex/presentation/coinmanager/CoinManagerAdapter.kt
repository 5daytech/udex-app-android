package com.blocksdecoded.dex.presentation.coinmanager

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.ui.getAttr
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_coin_disabled.*
import kotlinx.android.synthetic.main.item_coin_enabled.*

class CoinManagerAdapter(
    private var listener: Listener,
    private var startDragListener: IDragListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoinDragHelperCallback.Listener {
    lateinit var viewModel: CoinManagerViewModel

    private val typeEnabled = 0
    private val typeDisabled = 1
    private val typeDivider = 2

    private val showDivider
        get() = viewModel.enabledCoinsCount > 0

    override fun getItemCount() = viewModel.enabledCoinsCount + viewModel.disabledCoinsCount + (if (showDivider) 1 else 0)

    override fun getItemViewType(position: Int): Int = when {
        position < viewModel.enabledCoinsCount -> typeEnabled
        showDivider && position == viewModel.enabledCoinsCount -> typeDivider
        else -> typeDisabled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            typeEnabled -> ViewHolderEnabledCoin(parent.inflate(R.layout.item_coin_enabled))
            typeDisabled -> ViewHolderDisabledCoin(parent.inflate(R.layout.item_coin_disabled))
            else -> ViewHolderDivider(parent.inflate(R.layout.item_divider))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderEnabledCoin -> {
                val transactionRecord = viewModel.enabledItemForIndex(position)

                holder.onBind(
                    coin = transactionRecord,
                    canBeDisabled = viewModel.canBeDisabled(position),
                    onClick = { listener.onEnabledItemClick(it) }
                )

                holder.enabled_coin_drag.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        startDragListener.requestDrag(holder)
                    }
                    false
                }
            }
            is ViewHolderDisabledCoin -> {
                val transactionRecord = viewModel.disabledItemForIndex(disabledIndex(position))
                holder.onBind(
                    coin = transactionRecord,
                    onClick = { listener.onDisabledItemClick(disabledIndex(position)) }
                )
            }
        }
    }

    override fun onItemMoved(from: Int, to: Int) {
        notifyItemMoved(from, to)
    }

    override fun onItemMoveEnded(from: Int, to: Int) {
        viewModel.moveCoin(from, to)
    }

    private fun disabledIndex(position: Int): Int = when {
        showDivider -> position - viewModel.enabledCoinsCount - 1
        else -> position
    }

    interface Listener {
        fun onEnabledItemClick(position: Int)
        fun onDisabledItemClick(position: Int)
    }
}

class ViewHolderEnabledCoin(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun onBind(coin: Coin, canBeDisabled: Boolean, onClick: (position: Int) -> (Unit)) {
        val background = getAttr(if (canBeDisabled) R.attr.AccentBackground else R.attr.SmallActionButtonColor)
        itemView.setBackgroundColor(background)

        enabled_coin_title.text = coin.title
        enabled_coin_code.text = coin.code
        enabled_coin_icon.bind(coin.code)

        containerView.setOnClickListener { onClick.invoke(adapterPosition) }
    }
}

class ViewHolderDisabledCoin(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun onBind(coin: Coin, onClick: () -> (Unit)) {
        disabled_coin_title.text = coin.title
        disabled_coin_code.text = coin.code
        disabled_coin_icon.bind(coin.code)

        containerView.setOnClickListener { onClick.invoke() }
    }
}

class ViewHolderDivider(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer
