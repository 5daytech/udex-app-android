package com.blocksdecoded.dex.presentation.widgets.inputs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.utils.getAttr
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import kotlinx.android.synthetic.main.view_amount_input.view.*

class InputAmountView : ConstraintLayout {
    init {
        inflate(context, R.layout.view_amount_input, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun bindInitial(onMaxClick: (() -> (Unit))? = null, onSwitchClick: (() -> (Unit))? = null) {
        amount_switch.visibility = View.GONE
        amount_max.visibility = View.VISIBLE

        amount_max?.setOnClickListener { onMaxClick?.invoke() }
        amount_switch?.setOnClickListener { onSwitchClick?.invoke() }
        invalidate()
    }

    fun updateHint(hint: String? = null, error: String? = null) {
        amount_hint.visibility = if (error == null) View.VISIBLE else View.GONE
        amount_error.visibility = if (error == null) View.GONE else View.VISIBLE
        amount_hint.text = hint
        amount_error.text = error
    }

    fun updateHint(info: AmountInfo) {
        val enabledColor = context.theme.getAttr(R.attr.PrimaryTextColor) ?: 0
        val hintColor = context.theme.getAttr(R.attr.SecondaryHintTextColor) ?: 0
        val errorColor = ContextCompat.getColor(context, R.color.red)

        val amountInputColor = if (info.error == 0) enabledColor else errorColor
        val hintInputColor = if (info.error == 0) hintColor else errorColor

        amount_input?.setTextColor(amountInputColor)
        amount_hint?.setTextColor(hintInputColor)

        if (info.error == 0) {
            amount_hint?.text = context.getString(R.string.hint_you_send, info.value.toFiatDisplayFormat())
        } else {
            amount_hint?.setText(info.error)
        }

        amount_input?.setTextColor(amountInputColor)
    }

    fun enableSwitchBtn(enabled: Boolean) {
        amount_switch.isEnabled = enabled
    }

    fun updateAmountPrefix(prefix: String) {
        amount_prefix.text = prefix
    }

    fun setMaxBtnVisible(visible: Boolean) {
        amount_max.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
