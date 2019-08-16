package com.blocksdecoded.dex.presentation.exchange.view.market

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.widgets.listeners.SimpleTextWatcher
import com.blocksdecoded.dex.utils.ui.toLongDisplayFormat
import com.blocksdecoded.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_market_order.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class MarketOrderView: CardView {
	init { View.inflate(context, R.layout.view_market_order, this) }
	
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  { init() }
	
	var inputConnection: InputConnection? = null
	
	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	
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
					exchange_amount_input?.setText(newString)
					exchange_amount_input?.setSelection(newString.length)
				}
			}

			exchange_amount_max?.visible = amountText.isEmpty()
			sendAmountChangeSubject.onNext(amountNumber)
		}
	}

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
	fun updateState(state: MarketOrderViewState) {
		updateAmount(state.sendAmount)
		
		updateReceiveAmount(state.receiveAmount)
		
		exchange_base_spinner?.setSelectedPair(state.sendPair)
		exchange_quote_spinner?.setSelectedPair(state.receivePair)
	}
	
	private fun updateAmount(amount: BigDecimal) {
		if (amount > BigDecimal.ZERO) {
			exchange_amount_input?.setText(amount.stripTrailingZeros().toPlainString())
			exchange_amount_input?.setSelection(exchange_amount_input?.text?.length ?: 0)
		} else {
			exchange_amount_input?.setText("")
		}
	}
	
	private fun updateReceiveAmount(amount: BigDecimal) {
		if (amount > BigDecimal.ZERO) {
			exchange_receive_input?.text = amount.toLongDisplayFormat()
		} else {
			exchange_receive_input?.text = ""
		}
	}
}