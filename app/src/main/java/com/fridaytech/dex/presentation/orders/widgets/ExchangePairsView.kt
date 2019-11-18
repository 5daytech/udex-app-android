package com.fridaytech.dex.presentation.orders.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.orders.model.ExchangePairViewItem
import kotlinx.android.synthetic.main.view_current_pair.view.*

class ExchangePairsView : LinearLayout {
    init { View.inflate(context, R.layout.view_current_pair, this) }

    var selectedPair: Int = 0
        set(value) {
            field = value
            current_pair_drop_down?.let { spinner ->
                if (!spinner.isEmpty) current_pair_drop_down?.selectedItemPosition = value
            }
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun init(onSelectPair: (Int) -> Unit) {
        current_pair_drop_down?.init(onSelectPair)
    }

    fun refreshPairs(pairs: List<ExchangePairViewItem>) {
        current_pair_drop_down?.setData(pairs)
    }
}
