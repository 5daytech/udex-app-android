package com.blocksdecoded.dex.presentation.orders

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.ExchangePairViewItem
import com.blocksdecoded.dex.presentation.widgets.BaseDropDownView
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat

class ExchangePairsDropDown: BaseDropDownView<ExchangePairViewItem> {
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

        private class ExchangePairHolder (
            view: View,
            onItemPick: ((position: Int) -> Unit)? = null
        ) : DropDownHolder<ExchangePairViewItem> (view, onItemPick) {
            val baseCoin: TextView = itemView.findViewById(R.id.exchange_pair_base_coin)
            val quoteCoin: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin)
            val basePrice: TextView = itemView.findViewById(R.id.exchange_pair_base_coin_price)
            val quotePrice: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin_price)

            override fun onBind(data: ExchangePairViewItem) {
                baseCoin.text = data.baseCoin
                quoteCoin.text = data.quoteCoin
                basePrice.text = "$${data.basePrice.toFiatDisplayFormat()}"
                quotePrice.text = "$${data.quotePrice.toFiatDisplayFormat()}"
            }
        }
    }
}