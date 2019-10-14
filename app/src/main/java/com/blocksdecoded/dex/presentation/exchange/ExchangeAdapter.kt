package com.blocksdecoded.dex.presentation.exchange

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.blocksdecoded.dex.R

class ExchangeAdapter : PagerAdapter() {
	override fun instantiateItem(container: ViewGroup, position: Int): Any {
		val viewId = when(position) {
			0 -> R.id.exchange_market_view
			else -> R.id.exchange_limit_view
		}
		
		return container.findViewById(viewId)
	}
	
	override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

	override fun getCount(): Int = 2
	
	override fun getPageTitle(position: Int): CharSequence? = when(position) {
		0 -> "Market buy"
		else -> "Place order"
	}
}