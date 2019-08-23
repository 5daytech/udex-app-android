package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import com.blocksdecoded.dex.presentation.widgets.listeners.ItemSelectedListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage
import com.blocksdecoded.dex.utils.isValidIndex
import com.blocksdecoded.dex.utils.ui.toDisplayFormat

class CoinSpinnerView : Spinner {
	private var exchangeItems: List<ExchangePairItem> = listOf()
	private var coinsAdapter: CoinsSpinnerAdapter? = null
	
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
	
	fun init(onCoinSelected: (Int) -> Unit) {
		this.onItemSelectedListener = object: ItemSelectedListener() {
			override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) =
				onCoinSelected(position)
		}
		
		coinsAdapter = CoinsSpinnerAdapter(context, exchangeItems)
		this.adapter = coinsAdapter
	}
	
	fun setCoins(coins: List<ExchangePairItem>) {
		exchangeItems = coins
		coinsAdapter?.setCoins(exchangeItems)

		isEnabled = coins.size > 1
		backgroundTintList = ColorStateList.valueOf(
			ContextCompat.getColor(
				context,
				if (isEnabled) R.color.main_light_text else android.R.color.transparent
			)
		)
	}
	
	fun setSelectedPair(selectedPair: ExchangePairItem?) {
		val index = exchangeItems.indexOfFirst { it.code == selectedPair?.code ?: "" }
		if (index >= 0) {
			setSelection(index, true)
		}
	}

	fun getSelectedSymbol(): String = if (exchangeItems.isValidIndex(selectedItemPosition))
		exchangeItems[selectedItemPosition].code
	else
		""
	
	inner class CoinsSpinnerAdapter(
		internal var context: Context,
		private var coins: List<ExchangePairItem>
	) : BaseAdapter() {
		private var inflater: LayoutInflater = LayoutInflater.from(context)

		private fun getAdapterView(position: Int, convertView: View?, parent: ViewGroup?): View {
			val view = convertView ?: inflater.inflate(R.layout.item_coin_spinner, parent, false)

			view.findViewById<CoinIconImage>(R.id.item_coin_spinner_icon)?.bind(getItem(position)?.code)
			view.findViewById<TextView>(R.id.item_coin_spinner_title)?.text = getItem(position)?.code
			view.findViewById<TextView>(R.id.item_coin_spinner_balance)?.text =
				"${getItem(position)?.balance?.toDisplayFormat()} ${getItem(position)?.code}"

			return view
		}

		override fun getCount(): Int = coins.size

		override fun getItemId(p0: Int): Long = 0

		override fun getItem(position: Int): ExchangePairItem? = coins[position]

		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
			getAdapterView(position, convertView, parent)
		
		fun setCoins(coins: List<ExchangePairItem>) {
			this.coins = coins

			notifyDataSetChanged()
		}
	}
}