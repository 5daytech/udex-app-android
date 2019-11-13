package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.ui.CoinResUtil

class CoinIconView : ImageView {

    init {
        setImageResource(R.drawable.ic_coin_placeholder)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun bind(coinCode: String?) {
        val coinRes = CoinResUtil.getResForCoinCode(coinCode ?: "")
        setImageResource(if (coinRes == 0) R.drawable.ic_coin_placeholder else coinRes)
    }
}
