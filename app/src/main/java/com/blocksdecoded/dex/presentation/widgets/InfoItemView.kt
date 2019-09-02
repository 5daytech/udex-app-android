package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.transactions.TransactionStatus
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.view_info_item.view.*
import java.math.BigDecimal
import java.util.*

class InfoItemView: ConstraintLayout {

    private var dividerVisible = true
        set(value) {
            field = value
            base_info_divider?.visible = value
        }

    private var name: String = "name"
        set(value) {
            field = value
            base_info_title?.text = value
        }

    init { View.inflate(context, R.layout.view_info_item, this) }

    constructor(context: Context?) : super(context) { init(null) }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.InfoItemView, 0, 0)
            try {
                val nameResId = ta.getResourceId(R.styleable.InfoItemView_iiv_name, 0)
                name = if (nameResId > 0) {
                    context.getString(nameResId)
                } else {
                    ta.getString(R.styleable.InfoItemView_iiv_name) ?: "Attr"
                }

                dividerVisible = ta.getBoolean(R.styleable.InfoItemView_iiv_visible_divider, true)
            } finally {
                ta.recycle()
            }
        }
    }

    private fun resetAllViews() {
        base_info_text_value?.visible = false
        base_info_address_value?.visible = false
    }

    fun setRate(coin: Coin, rate: BigDecimal?) {
        resetAllViews()
        base_info_text_value?.text = "$${rate?.toFiatDisplayFormat()} per ${coin.code}"
        base_info_text_value?.visible = true
    }

    fun setAddress(address: String?) {
        resetAllViews()
        address?.let {
            base_info_address_value?.update(it)
            base_info_address_value?.visible = true
        }
    }

    fun setFiat(amount: BigDecimal?, isExactAmount: Boolean = true) {
        resetAllViews()
        base_info_text_value?.text = "${if (isExactAmount) "" else "~ "}$${amount?.toFiatDisplayFormat()}"
        base_info_text_value?.visible = true
    }

    fun setDate(date: Date?) {
        resetAllViews()
        date?.let {
            base_info_text_value?.text = "${TimeUtils.dateToDisplayFormat(date)}"
            base_info_text_value?.visible = true
        }
    }

    fun setStatus(status: TransactionStatus?) {
        resetAllViews()
        base_info_text_value?.text = "Confirmed"
        base_info_text_value?.visible = true
    }

}