package com.blocksdecoded.dex.presentation.exchange.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.TextWatcher
import com.blocksdecoded.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_exchange.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class ExchangeView: CardView {
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  { init() }

	init { View.inflate(context, R.layout.view_exchange, this) }
	
	var inputConnection: InputConnection? = null
	
	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	val receiveAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	
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
					exchange_amount_input?.setText(newString)
					exchange_amount_input?.setSelection(newString.length)
				}
			}

			exchange_amount_max?.visible = amountText.isEmpty()
			sendAmountChangeSubject.onNext(amountNumber)
		}
	}
	
//	private val receiveAmountChangeWatcher = object: TextWatcher() {
//		override fun afterTextChanged(s: Editable?) {
//			val amountText = s?.toString() ?: ""
//			var amountNumber = when {
//				amountText != "" -> amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
//				else -> BigDecimal.ZERO
//			}
//
//			val decimalSize = 18
//			decimalSize.let {
//				if (amountNumber.scale() > it) {
//					amountNumber = amountNumber.setScale(it, RoundingMode.FLOOR)
//					val newString = amountNumber.toPlainString()
//					exchange_amount_input?.setText(newString)
//					exchange_amount_input?.setSelection(newString.length)
//				}
//			}
//
//			exchange_amount_max?.visible = amountText.isEmpty()
//			sendAmountChangeSubject.onNext(amountNumber)
//		}
//	}

	private fun init() {
		exchange_amount_input?.addTextChangedListener(sendAmountChangeWatcher)
		exchange_amount_input?.showSoftInputOnFocus = false
		inputConnection = exchange_amount_input?.onCreateInputConnection(EditorInfo())
	}

	fun bind(
		onMaxClick: () -> Unit,
		onSendCoinPick: (Int) -> Unit,
		onReceiveCoinPick: (Int) -> Unit,
		onSwitchClick: () -> Unit
	) {
		exchange_base_spinner?.init(onSendCoinPick)
		exchange_quote_spinner?.init(onReceiveCoinPick)
		exchange_amount_max?.setOnClickListener { onMaxClick() }
		exchange_switch?.setOnClickListener { onSwitchClick() }
	}
	
	fun updateSendCoins(coins: List<ExchangePairItem>) {
		exchange_base_spinner?.setCoins(coins)
	}
	
	fun updateReceiveCoins(coins: List<ExchangePairItem>) {
		exchange_quote_spinner?.setCoins(coins)
	}
	
	@SuppressLint("SetTextI18n")
	fun updateState(state: ExchangeViewState) {
		Log.d("ololo", "Update state $state")
		val amount = state.sendAmount
		if (amount > BigDecimal.ZERO) {
			exchange_amount_input?.setText(amount.stripTrailingZeros().toPlainString())
			exchange_amount_input?.setSelection(exchange_amount_input?.text?.length ?: 0)
		} else {
			exchange_amount_input?.setText("")
		}
		
		exchange_receive_input?.text = state.receiveAmount.stripTrailingZeros().toString()
		
		exchange_base_spinner?.setSelectedPair(state.sendPair)
		exchange_quote_spinner?.setSelectedPair(state.receivePair)
	}
}