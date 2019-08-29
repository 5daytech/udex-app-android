package com.blocksdecoded.dex.presentation.transactions.info

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.R

class TransactionInfoItemView: ConstraintLayout {

    init { View.inflate(context, R.layout.view_transaction_info_item, this) }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

}