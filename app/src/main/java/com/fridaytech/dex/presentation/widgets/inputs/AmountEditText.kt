package com.fridaytech.dex.presentation.widgets.inputs

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import com.fridaytech.dex.presentation.widgets.listeners.SimpleTextWatcher
import java.math.BigDecimal
import java.math.RoundingMode

class AmountEditText : EditText {
    val decimalSize = 18
    private var changeListener: ((amount: BigDecimal) -> Unit)? = null
    private var decimalProvider: (() -> Int?)? = null

    private val amountTextWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            val amountText = s?.toString() ?: ""
            var amountNumber = when {
                amountText != "" -> amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
                else -> BigDecimal.ZERO
            }

            (decimalProvider?.invoke() ?: decimalSize).let {
                if (amountNumber.scale() > it) {
                    amountNumber = amountNumber.setScale(it, RoundingMode.FLOOR)
                    val newString = amountNumber.toPlainString()
                    this@AmountEditText.setText(newString)
                    this@AmountEditText.setSelection(newString.length)
                }
            }

            changeListener?.invoke(amountNumber)
        }
    }

    constructor(context: Context?) : super(context) { init() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init() }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { init() }

    fun init() {
        addTextChangedListener(amountTextWatcher)
    }

    fun bind(onChange: (amount: BigDecimal) -> Unit): InputConnection =
        bind(onChange, null)

    fun bind(
        onChange: (amount: BigDecimal) -> Unit,
        decimalProvider: (() -> Int?)? = null
    ): InputConnection {
        changeListener = onChange
        this.decimalProvider = decimalProvider

        showSoftInputOnFocus = false

        return onCreateInputConnection(EditorInfo())
    }
}
