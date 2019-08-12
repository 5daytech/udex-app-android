package com.blocksdecoded.dex.presentation.exchange.view.limit

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.listeners.TextWatcher
import com.blocksdecoded.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_limit_order.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class LimitOrderView: CardView {
	init { View.inflate(context, R.layout.view_limit_order, this) }
	
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	
	var inputConnection: InputConnection? = null
	
	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	val priceChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	
	private val sendAmountChangeWatcher = object: TextWatcher() {
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
					limit_amount_input?.setText(newString)
					limit_amount_input?.setSelection(newString.length)
				}
			}
			
			limit_amount_input?.visible = amountText.isEmpty()
			sendAmountChangeSubject.onNext(amountNumber)
		}
	}
	
	fun init() {
		limit_amount_input?.addTextChangedListener(sendAmountChangeWatcher)
		limit_amount_input?.showSoftInputOnFocus = false
		inputConnection = limit_amount_input?.onCreateInputConnection(EditorInfo())
		
		
	}
}