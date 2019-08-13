package com.blocksdecoded.dex.presentation.exchange.view.limit

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.listeners.SimpleTextWatcher
import com.blocksdecoded.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_limit_order.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class LimitOrderView: CardView {
	init { View.inflate(context, R.layout.view_limit_order, this) }
	
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }
	
	var amountInputConnection: InputConnection? = null
	var priceInputConnection: InputConnection? = null
	
	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	val priceChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	
	private val sendAmountChangeWatcher = object: SimpleTextWatcher() {
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
			
			limit_amount_max?.visible = amountText.isEmpty()
			sendAmountChangeSubject.onNext(amountNumber)
		}
	}
	
	private val priceChangeWatcher = object: SimpleTextWatcher() {
		override fun afterTextChanged(s: Editable?) {
			val priceText = s?.toString() ?: ""
			var priceNumber = when {
				priceText != "" -> priceText.toBigDecimalOrNull() ?: BigDecimal.ZERO
				else -> BigDecimal.ZERO
			}
			
			val decimalSize = 18
			decimalSize.let {
				if (priceNumber.scale() > it) {
					priceNumber = priceNumber.setScale(it, RoundingMode.FLOOR)
					val newString = priceNumber.toPlainString()
					limit_price_input?.setText(newString)
					limit_price_input?.setSelection(newString.length)
				}
			}
			
			priceChangeSubject.onNext(priceNumber)
		}
	}
	
	fun init() {
		limit_amount_input?.addTextChangedListener(sendAmountChangeWatcher)
		limit_amount_input?.showSoftInputOnFocus = false
		amountInputConnection = limit_amount_input?.onCreateInputConnection(EditorInfo())
		
		limit_price_input?.addTextChangedListener(priceChangeWatcher)
		limit_price_input?.showSoftInputOnFocus = false
		priceInputConnection = limit_price_input?.onCreateInputConnection(EditorInfo())
	}
}