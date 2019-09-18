package com.blocksdecoded.dex.presentation.orders

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.ExchangePairViewItem
import kotlinx.android.synthetic.main.view_current_pair.view.*

class ExchangePairsView: LinearLayout {
    init { View.inflate(context, R.layout.view_current_pair, this) }
    
    var selectedPair: Int = 0
        set(value) {
            field = value
            current_pair_spinner?.let { spinner ->
                if (!spinner.adapter.isEmpty) current_pair_spinner?.setSelection(value)
            }
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    
    fun init(onSelectPair: (Int) -> Unit) {
        current_pair_spinner?.init(onSelectPair)
    }
    
    fun refreshPairs(pairs: List<ExchangePairViewItem>) {
        current_pair_spinner?.setExchangePairs(pairs)
    }
}