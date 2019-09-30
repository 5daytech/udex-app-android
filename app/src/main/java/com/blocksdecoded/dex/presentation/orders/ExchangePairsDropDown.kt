package com.blocksdecoded.dex.presentation.orders

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import androidx.core.view.isInvisible
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.ExchangePairViewItem
import com.blocksdecoded.dex.presentation.widgets.BaseDropDownView
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible

class ExchangePairsDropDown: BaseDropDownView<ExchangePairViewItem> {
    override val popupVerticalOffset: Int = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun refreshSelectedItem(item: ExchangePairViewItem) {
        val baseCoin: TextView? = selectedView?.findViewById(R.id.exchange_pair_base_coin)
        val quoteCoin: TextView? = selectedView?.findViewById(R.id.exchange_pair_quote_coin)
        val basePrice: TextView? = selectedView?.findViewById(R.id.exchange_pair_base_coin_price)
        val quotePrice: TextView? = selectedView?.findViewById(R.id.exchange_pair_quote_coin_price)

        baseCoin?.text = item.baseCoin
        quoteCoin?.text = item.quoteCoin
        basePrice?.text = "$${item.basePrice?.toFiatDisplayFormat()}"
        quotePrice?.text = "$${item.quotePrice?.toFiatDisplayFormat()}"

        selectedView?.findViewById<View>(R.id.divider)?.visible = false
    }

    fun init(onItemPick: (position: Int) -> Unit) {
        val adapter = OrdersPopupAdapter(listOf())
        init(adapter, onItemPick)
    }

    private class OrdersPopupAdapter(
        items: List<ExchangePairViewItem>,
        onItemPick: ((position: Int) -> Unit)? = null
    ): BaseDropDownView.PopupAdapter<ExchangePairViewItem>(items, onItemPick) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropDownHolder<ExchangePairViewItem> =
            ExchangePairHolder(parent.inflate(R.layout.item_exchange_pair), onItemPick)

        override fun onBindViewHolder(holder: DropDownHolder<ExchangePairViewItem>, position: Int) {
            super.onBindViewHolder(holder, position)
            (holder as ExchangePairHolder).divider.isInvisible = position == itemCount - 1
        }

        private class ExchangePairHolder (
            view: View,
            onItemPick: ((position: Int) -> Unit)? = null
        ) : DropDownHolder<ExchangePairViewItem> (view, onItemPick) {
            val baseCoin: TextView = itemView.findViewById(R.id.exchange_pair_base_coin)
            val quoteCoin: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin)
            val basePrice: TextView = itemView.findViewById(R.id.exchange_pair_base_coin_price)
            val quotePrice: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin_price)
            val divider: View = itemView.findViewById(R.id.divider)

            override fun onBind(data: ExchangePairViewItem) {
                baseCoin.text = data.baseCoin
                quoteCoin.text = data.quoteCoin
                basePrice.text = "$${data.basePrice.toFiatDisplayFormat()}"
                quotePrice.text = "$${data.quotePrice.toFiatDisplayFormat()}"
            }
        }
    }
}