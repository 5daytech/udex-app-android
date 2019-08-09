package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.blocksdecoded.dex.presentation.widgets.ItemSelectedListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.CoinIconImage


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
		coinsAdapter?.setCoins(coins)
	}
	
	fun setSelectedPair(selectedPair: ExchangePairItem?) {
		val index = exchangeItems.indexOfFirst { it.code == selectedPair?.code ?: "" }
		if (index >= 0) {
			setSelection(index)
		}
	}
	
	inner class CoinsSpinnerAdapter(
		internal var context: Context,
		private var coins: List<ExchangePairItem>
	) : BaseAdapter() {
		private var inflater: LayoutInflater = LayoutInflater.from(context)
		
		private fun getAdapterView(position: Int, parent: ViewGroup?): View {
			val itemView = inflater.inflate(R.layout.item_coin_spinner, parent, false)
			
			itemView.findViewById<CoinIconImage>(R.id.item_coin_spinner_icon).bind(coins[position].code)
			itemView.findViewById<TextView>(R.id.item_coin_spinner_title).text = coins[position].code
			
			return itemView
		}
		
		override fun getCount(): Int = coins.size
		override fun getItem(i: Int): Any? = null
		override fun getItemId(i: Int): Long = 0
		
		override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View =
			getAdapterView(position, parent)
		
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
			getAdapterView(position, parent)
		
		fun setCoins(coins: List<ExchangePairItem>) {
			this.coins = coins
			notifyDataSetChanged()
		}
	}
}