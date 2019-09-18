package com.blocksdecoded.dex.presentation.orders

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
import com.blocksdecoded.dex.presentation.orders.model.ExchangePairViewItem
import com.blocksdecoded.dex.utils.ui.DimenUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat

class ExchangePairsSpinner : Spinner {
	private var exchangeItems: List<ExchangePairViewItem> = listOf()
	private var coinsAdapter: ExchangePairsAdapter? = null
	
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        dropDownVerticalOffset = height - DimenUtils.dp(6f)
    }

	private fun updateEnabled() {
		isEnabled = exchangeItems.size > 1
		backgroundTintList = ColorStateList.valueOf(
			ContextCompat.getColor(
				context,
				if (isEnabled) R.color.main_light_text else android.R.color.transparent
			)
		)
	}

	fun init(onCoinSelected: (Int) -> Unit) {
		this.onItemSelectedListener = object: ItemSelectedListener() {
			override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) =
				onCoinSelected(position)
		}

		coinsAdapter = ExchangePairsAdapter(context, exchangeItems)
		this.adapter = coinsAdapter
	}

	fun setExchangePairs(coins: List<ExchangePairViewItem>) {
		exchangeItems = coins
		coinsAdapter?.setCoins(exchangeItems)
		updateEnabled()
	}

    inner class ExchangePairsAdapter(
        internal var context: Context,
        private var exchangePairs: List<ExchangePairViewItem>
    ) : BaseAdapter() {
        private var inflater: LayoutInflater = LayoutInflater.from(context)

        private fun getAdapterView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.item_exchange_pair, parent, false)

            view.findViewById<TextView>(R.id.exchange_pair_base_coin)?.text = getItem(position)?.baseCoin
            view.findViewById<TextView>(R.id.exchange_pair_quote_coin)?.text = getItem(position)?.quoteCoin

            view.findViewById<TextView>(R.id.exchange_pair_base_coin_price)?.text =
                "$${getItem(position)?.basePrice?.toFiatDisplayFormat()}"
            view.findViewById<TextView>(R.id.exchange_pair_quote_coin_price)?.text =
                "$${getItem(position)?.quotePrice?.toFiatDisplayFormat()}"

            return view
        }

        override fun getCount(): Int = exchangePairs.size

        override fun getItemId(p0: Int): Long = 0

        override fun getItem(position: Int): ExchangePairViewItem? = exchangePairs[position]

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? =
            getAdapterView(position, convertView, parent)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            return if (position == selectedItemPosition) {
                View(context)
            } else {
                super.getDropDownView(position, null, parent)
            }
        }

        fun setCoins(coins: List<ExchangePairViewItem>) {
            this.exchangePairs = coins

            notifyDataSetChanged()
        }
    }
}