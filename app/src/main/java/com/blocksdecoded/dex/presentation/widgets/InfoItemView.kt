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
import com.blocksdecoded.dex.utils.ui.toLongDisplayFormat
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.view_info_item.view.*
import java.math.BigDecimal
import java.util.*

class InfoItemView: ConstraintLayout {

    private var dividerVisible = true
        set(value) {
            field = value
            info_divider?.visible = value
        }

    private var name: String = "name"
        set(value) {
            field = value
            info_title?.text = value
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
                val nameResId = ta.getResourceId(R.styleable.InfoItemView_iiv_title, 0)
                name = if (nameResId > 0) {
                    context.getString(nameResId)
                } else {
                    ta.getString(R.styleable.InfoItemView_iiv_title) ?: "Attr"
                }

                dividerVisible = ta.getBoolean(R.styleable.InfoItemView_iiv_visible_divider, true)
            } finally {
                ta.recycle()
            }
        }
    }

    private fun resetAllViews() {
        info_text_value?.visible = false
        info_address_value?.visible = false
    }

    fun setRate(coin: Coin, rate: BigDecimal?) {
        resetAllViews()
        info_text_value?.text = "$${rate?.toFiatDisplayFormat()} per ${coin.code}"
        info_text_value?.visible = true
    }

    fun setCoin(coinCode: String, amount: BigDecimal?, isExactAmount: Boolean = true) {
        resetAllViews()
        info_text_value?.text = "${if (isExactAmount) "" else "~ "}${amount?.toLongDisplayFormat()} $coinCode"
        info_text_value?.visible = true
    }

    fun setCoinWithFiat(coin: Coin, amount: BigDecimal?, fiatAmount: BigDecimal?) {
        resetAllViews()
        info_text_value?.text = "${amount?.toLongDisplayFormat()} ${coin.code} ~ $${fiatAmount?.toFiatDisplayFormat()}"
        info_text_value?.visible = true
    }

    fun setAddress(address: String?) {
        resetAllViews()
        address?.let {
            info_address_value?.update(it)
            info_address_value?.visible = true
        }
    }

    fun setFiat(amount: BigDecimal?, isExactAmount: Boolean = true) {
        resetAllViews()
        info_text_value?.text = "${if (isExactAmount) "" else "~ "}$${amount?.toFiatDisplayFormat()}"
        info_text_value?.visible = true
    }

    fun setDate(date: Date?) {
        resetAllViews()
        date?.let {
            info_text_value?.text = "${TimeUtils.dateToDisplayFormat(date)}"
            info_text_value?.visible = true
        }
    }

    fun setMillis(time: Long?) {
        resetAllViews()
        time?.let {
            info_text_value?.text = "~ ${TimeUtils.millisToShort(time)} s."
            info_text_value?.visible = true
        }
    }

    fun setRaw(raw: String?) {
        resetAllViews()
        raw?.let {
            info_text_value?.text = raw
            info_text_value?.visible = true
        }
    }

    fun setStatus(status: TransactionStatus?) {
        resetAllViews()
        info_text_value?.text = "Confirmed"
        info_text_value?.visible = true
    }

}