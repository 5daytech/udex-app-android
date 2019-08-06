package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.ItemSelectedListener
import kotlinx.android.synthetic.main.view_current_pair.view.*

class CoinSpinnerView : Spinner {
	
	private var exchangeItems: List<ExchangePairItem> = listOf()
	
	init {
	
	}
	
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
	
	fun init(onSelectCoin: (Int) -> Unit) {
		this.onItemSelectedListener = object: ItemSelectedListener() {
			override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) =
				onSelectCoin(position)
		}
	}
	
	fun setCoins(coins: List<ExchangePairItem>) {
		exchangeItems = coins
		val adapter = ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item)
		adapter.addAll(coins.map { it.code })
		
		this.adapter = adapter
	}
	
	fun setSelectedPair(selectedPair: ExchangePairItem) {
		val index = exchangeItems.indexOfFirst { it.code == selectedPair.code }
		if (index >= 0) {
			setSelection(index)
		}
	}
}