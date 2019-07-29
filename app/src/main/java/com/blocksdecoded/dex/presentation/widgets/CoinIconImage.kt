package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.blocksdecoded.dex.R

class CoinIconImage: ImageView {

    init {
        setImageResource(R.drawable.ic_coin_placeholder)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun bind(coinCode: String) {
        //TODO: Load coin icon from coinCode
        setImageResource(R.drawable.ic_coin_placeholder)
    }
}