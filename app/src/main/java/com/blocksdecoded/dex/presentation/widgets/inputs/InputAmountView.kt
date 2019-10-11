package com.blocksdecoded.dex.presentation.widgets.inputs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.R
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
        amount_max.visibility =  View.VISIBLE

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
