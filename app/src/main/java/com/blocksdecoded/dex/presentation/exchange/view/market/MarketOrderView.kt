package com.blocksdecoded.dex.presentation.exchange.view.market

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangePairsInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeReceiveInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.MarketOrderViewState
import com.blocksdecoded.dex.utils.listeners.SimpleTextWatcher
import com.blocksdecoded.dex.utils.ui.AnimationHelper
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
	
	var sendInputConnection: InputConnection? = null
	var receiveInputConnection: InputConnection? = null

	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	val receiveAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()

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
					market_amount_input?.setText(newString)
					market_amount_input?.setSelection(newString.length)
				}
			}

			market_amount_max?.visible = amountText.isEmpty()
			sendAmountChangeSubject.onNext(amountNumber)
		}
	}

	private val receiveAmountChangeWatcher = object: SimpleTextWatcher() {
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
					market_amount_input?.setText(newString)
					market_amount_input?.setSelection(newString.length)
				}
			}

			market_amount_max?.visible = amountText.isEmpty()
			receiveAmountChangeSubject.onNext(amountNumber)
		}
	}

	private fun init() {
		market_amount_input?.addTextChangedListener(sendAmountChangeWatcher)
		market_amount_input?.showSoftInputOnFocus = false
		sendInputConnection = market_amount_input?.onCreateInputConnection(EditorInfo())

		market_receive_input?.addTextChangedListener(receiveAmountChangeWatcher)
		market_receive_input?.showSoftInputOnFocus = false
		receiveInputConnection = market_receive_input?.onCreateInputConnection(EditorInfo())
	}

	fun bind(
		onMaxClick: () -> Unit,
		onSendCoinPick: (Int) -> Unit,
		onReceiveCoinPick: (Int) -> Unit,
		onSwitchClick: () -> Unit
	) {
		market_base_spinner?.init(onSendCoinPick)
		market_quote_spinner?.init(onReceiveCoinPick)
		market_amount_max?.setOnClickListener { onMaxClick() }
		market_switch?.setOnClickListener {
			onSwitchClick()
			AnimationHelper.rotate(market_switch)
		}
	}
	
	fun updateSendCoins(info: ExchangePairsInfo) {
		market_base_spinner?.setData(info.coins)
		market_base_spinner?.setSelectedPair(info.selectedCoin)
	}
	
	fun updateReceiveCoins(info: ExchangePairsInfo) {
		market_quote_spinner?.setData(info.coins)
		market_quote_spinner?.setSelectedPair(info.selectedCoin)
	}

	fun updateReceiveInfo(receiveInfo: ExchangeReceiveInfo) {
		updateReceiveAmount(receiveInfo.receiveAmount)
	}
	
	@SuppressLint("SetTextI18n")
	fun updateState(state: MarketOrderViewState) {
		updateAmount(state.sendAmount)
		
		updateReceiveAmount(state.receiveAmount)
		
		market_base_spinner?.setSelectedPair(state.sendCoin)
		market_quote_spinner?.setSelectedPair(state.receiveCoin)
	}
	
	private fun updateAmount(amount: BigDecimal) {
		if (amount > BigDecimal.ZERO) {
			market_amount_input?.setText(amount.stripTrailingZeros().toPlainString())
			market_amount_input?.setSelection(market_amount_input?.text?.length ?: 0)
		} else {
			market_amount_input?.setText("")
		}
	}
	
	private fun updateReceiveAmount(amount: BigDecimal) {
		val text = if (amount > BigDecimal.ZERO) {
			amount.toLongDisplayFormat()
		} else {
			""
		}

		market_receive_input?.setText(text, TextView.BufferType.EDITABLE)
	}
}