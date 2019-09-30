package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.marginLeft
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeCoinItem
import com.blocksdecoded.dex.presentation.widgets.BaseDropDownView
import com.blocksdecoded.dex.presentation.widgets.CoinIconView
import com.blocksdecoded.dex.utils.dp
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.view_drop_down.view.*

class CoinDropDown : BaseDropDownView<ExchangeCoinItem> {
	override val popupVerticalOffset: Int
		get() = dp(-4f)

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun refreshSelectedItem(item: ExchangeCoinItem) {
		selectedView?.findViewById<View>(R.id.divider)?.visible = false
		selectedView?.findViewById<CoinIconView>(R.id.item_coin_spinner_icon)?.bind(item.code)
		selectedView?.findViewById<TextView>(R.id.item_coin_spinner_title)?.text = item.code
		selectedView?.findViewById<TextView>(R.id.item_coin_spinner_balance)?.text =
					"${item.balance?.toDisplayFormat()} ${item.code}"
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
		val adapter = CoinDropDownAdapter(listOf())
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
	): BaseDropDownView.PopupAdapter<ExchangeCoinItem>(items, onItemPick) {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinHolder =
			CoinHolder(parent.inflate(R.layout.item_coin_spinner), onItemPick)

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
				balance.text = "${data.balance?.toDisplayFormat()} ${data.code}"
			}
		}
	}
}