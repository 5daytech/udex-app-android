package com.fridaytech.dex.presentation.exchange

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.marginLeft
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.exchange.model.ExchangeCoinItem
import com.fridaytech.dex.presentation.widgets.BaseDropDownView
import com.fridaytech.dex.presentation.widgets.CoinIconView
import com.fridaytech.dex.utils.inflate
import com.fridaytech.dex.utils.isValidIndex
import com.fridaytech.dex.utils.ui.toDisplayFormat
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.view_drop_down.view.*

class CoinDropDown :
    BaseDropDownView<ExchangeCoinItem> {
    override val popupVerticalOffset: Int = 0
// 		get() = dp(-4f)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun refreshSelectedItem(item: ExchangeCoinItem) {
        selectedView?.findViewById<View>(R.id.divider)?.visible = false
        selectedView?.findViewById<CoinIconView>(R.id.item_coin_spinner_icon)?.bind(item.code)
        selectedView?.findViewById<TextView>(R.id.item_coin_spinner_title)?.text = item.code
        selectedView?.findViewById<TextView>(R.id.item_coin_spinner_balance)?.text =
                    "Balance:\n${item.balance?.toDisplayFormat()}"
        selectedView?.setBackgroundResource(R.color.transparent)
    }

    private fun refreshSelectedViewPadding() {
        val arrowHorizontalSpace = (drop_down_arrow?.marginLeft ?: 0) + (drop_down_arrow?.width ?: 0)

        selectedView?.setPadding(
            selectedView?.paddingLeft ?: 0,
            selectedView?.paddingTop ?: 0,
            arrowHorizontalSpace,
            selectedView?.paddingBottom ?: 0
        )
    }

    fun init(onCoinSelected: (Int) -> Unit) {
        val adapter =
            CoinDropDownAdapter(listOf())
        init(adapter, onCoinSelected)
    }

    override fun inflateSelectedView() {
        super.inflateSelectedView()
        refreshSelectedViewPadding()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        refreshSelectedViewPadding()
    }

    fun setSelectedPair(selectedCoin: ExchangeCoinItem?) {
        val index = popupAdapter?.items?.indexOfFirst { it.code == selectedCoin?.code ?: "" } ?: -1
        if (index >= 0) {
            selectedItemPosition = index
        }
    }

    fun getSelectedSymbol(): String = if (popupAdapter?.items.isValidIndex(selectedItemPosition ?: -1))
        popupAdapter?.items?.get(selectedItemPosition ?: 0)?.code ?: ""
    else
        ""

    private class CoinDropDownAdapter(
        items: List<ExchangeCoinItem>,
        onItemPick: ((position: Int) -> Unit)? = null
    ) : BaseDropDownView.PopupAdapter<ExchangeCoinItem>(items, onItemPick) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinHolder =
            CoinHolder(
                parent.inflate(R.layout.item_coin_spinner),
                onItemPick
            )

        override fun onBindViewHolder(holder: DropDownHolder<ExchangeCoinItem>, position: Int) {
            super.onBindViewHolder(holder, position)
            (holder as CoinHolder).divider.isInvisible = position == itemCount - 1
        }

        private class CoinHolder(
            view: View,
            onItemPick: ((position: Int) -> Unit)? = null
        ) : DropDownHolder<ExchangeCoinItem> (view, onItemPick) {

            val coinIcon: CoinIconView = itemView.findViewById(R.id.item_coin_spinner_icon)
            val title: TextView = itemView.findViewById(R.id.item_coin_spinner_title)
            val balance: TextView = itemView.findViewById(R.id.item_coin_spinner_balance)
            val divider: View = itemView.findViewById(R.id.divider)

            override fun onBind(data: ExchangeCoinItem) {
                coinIcon.bind(data.code)
                title.text = data.code
                balance.text = "Balance:\n${data.balance.toDisplayFormat()}"
            }
        }
    }
}
