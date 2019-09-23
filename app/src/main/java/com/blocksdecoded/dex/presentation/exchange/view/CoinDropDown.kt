package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeCoinItem
import com.blocksdecoded.dex.presentation.widgets.BaseDropDownView
import com.blocksdecoded.dex.presentation.widgets.CoinIconView
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import kotlinx.android.synthetic.main.view_drop_down.view.*

class CoinDropDown : BaseDropDownView<ExchangeCoinItem> {
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun refreshSelectedItem(item: ExchangeCoinItem) {
		selectedView?.findViewById<CoinIconView>(R.id.item_coin_spinner_icon)?.bind(item.code)
		selectedView?.findViewById<TextView>(R.id.item_coin_spinner_title)?.text = item.code
		selectedView?.findViewById<TextView>(R.id.item_coin_spinner_balance)?.text =
					"${item.balance?.toDisplayFormat()} ${item.code}"
	}

	private fun updateEnabled() {
		isEnabled = popupAdapter?.itemCount ?: 0 > 1
		drop_down_arrow?.isInvisible = !isEnabled
	}

	fun init(onCoinSelected: (Int) -> Unit) {
		val adapter = CoinDropDownAdapter(listOf())
		init(adapter, onCoinSelected)
	}

	override fun setData(data: List<ExchangeCoinItem>) {
		super.setData(data)
		updateEnabled()
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
	): BaseDropDownView.PopupAdapter<ExchangeCoinItem>(items, onItemPick) {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinHolder =
			CoinHolder(parent.inflate(R.layout.item_coin_spinner), onItemPick)

		private class CoinHolder(
			view: View,
			onItemPick: ((position: Int) -> Unit)? = null
		) : DropDownHolder<ExchangeCoinItem> (view, onItemPick) {

			val coinIcon: CoinIconView = itemView.findViewById(R.id.item_coin_spinner_icon)
			val title: TextView = itemView.findViewById(R.id.item_coin_spinner_title)
			val balance: TextView = itemView.findViewById(R.id.item_coin_spinner_balance)

			override fun onBind(data: ExchangeCoinItem) {
				coinIcon.bind(data.code)
				title.text = data.code
				balance.text = "${data.balance?.toDisplayFormat()} ${data.code}"
			}
		}
	}
}