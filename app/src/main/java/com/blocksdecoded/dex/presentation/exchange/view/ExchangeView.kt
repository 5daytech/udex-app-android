package com.blocksdecoded.dex.presentation.exchange.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.ExchangeViewModel
import com.blocksdecoded.dex.presentation.widgets.TextWatcher
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.view_exchange.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class ExchangeView: CardView {
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  { init() }

	init { View.inflate(context, R.layout.view_exchange, this) }

	val viewModel: ExchangeViewModel? = null
	var inputConnection: InputConnection? = null
	
	private val amountChangeListener = object: TextWatcher() {
		override fun afterTextChanged(s: Editable?) {
			val amountText = s?.toString() ?: ""
			var amountNumber = when {
				amountText != "" -> amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
				else -> BigDecimal.ZERO
			}

			val decimalSize = 18
			decimalSize.let {
				if (amountNumber.scale() > it) {
					amountNumber = amountNumber.setScale(it, RoundingMode.FLOOR)
					val newString = amountNumber.toPlainString()
					exchange_amount_input?.setText(newString)
					exchange_amount_input?.setSelection(newString.length)

//					val shake = AnimationUtils.loadAnimation(applicationContext, R.anim.shake_edittext)
//					exchange_amount_input.startAnimation(shake)
				}
			}

			exchange_amount_max?.visible = amountText.isEmpty()
//			amountChangeSubject.onNext(amountNumber)
		}
	}

	//region Constructor



	//endregion

	private fun init() {
		exchange_amount_input?.addTextChangedListener(amountChangeListener)
		exchange_amount_input?.showSoftInputOnFocus = false
		inputConnection = exchange_amount_input?.onCreateInputConnection(EditorInfo())
	}

	fun bind(
		onMaxClick: () -> Unit
	) {

	}
	
    fun updateInput(input: String?) {
    
    }
}