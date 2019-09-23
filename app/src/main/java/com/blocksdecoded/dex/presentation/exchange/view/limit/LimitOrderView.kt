package com.blocksdecoded.dex.presentation.exchange.view.limit

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangePairsInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangePriceInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.ExchangeReceiveInfo
import com.blocksdecoded.dex.presentation.exchange.view.model.LimitOrderViewState
import com.blocksdecoded.dex.presentation.widgets.listeners.SimpleTextWatcher
import com.blocksdecoded.dex.utils.ui.AnimationHelper
import com.blocksdecoded.dex.utils.ui.toLongDisplayFormat
import com.blocksdecoded.dex.utils.ui.toPriceFormat
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

	private var state: LimitOrderViewState? = null

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
	
	fun bind(
		onMaxClick: () -> Unit,
		onSendCoinPick: (Int) -> Unit,
		onReceiveCoinPick: (Int) -> Unit,
		onSwitchClick: () -> Unit
	) {
		limit_base_spinner?.init(onSendCoinPick)
		limit_quote_spinner?.init(onReceiveCoinPick)
		limit_amount_max?.setOnClickListener { onMaxClick() }
		limit_switch?.setOnClickListener {
			onSwitchClick()
			AnimationHelper.rotate(limit_switch)
		}
	}
	
	fun updateSendCoins(info: ExchangePairsInfo) {
		limit_base_spinner?.setData(info.coins)
		limit_base_spinner?.setSelectedPair(info.selectedCoin)
	}
	
	fun updateReceiveCoins(info: ExchangePairsInfo) {
		limit_quote_spinner?.setData(info.coins)
		limit_quote_spinner?.setSelectedPair(info.selectedCoin)
	}
	
	@SuppressLint("SetTextI18n")
	fun updateState(state: LimitOrderViewState) {
		this.state = state
		updateAmount(state.sendAmount)
		
		limit_base_spinner?.setSelectedPair(state.sendCoin)
		limit_quote_spinner?.setSelectedPair(state.receiveCoin)
	}

	fun updateTotal(totalInfo: ExchangeReceiveInfo) {
		updateTotal(totalInfo.receiveAmount)
	}

	fun updatePrice(priceInfo: ExchangePriceInfo) {
		updatePrice(priceInfo.sendPrice)
	}

	fun updateAveragePrice(price: BigDecimal) {
		val rawPrice = if (price > BigDecimal.ZERO) price.toPriceFormat() else "-"
		limit_receive_hint?.text = "Average ~$rawPrice"
	}
	
	private fun updateTotal(total: BigDecimal) {
		if (total > BigDecimal.ZERO) {
			limit_total?.text = "You Receive: ${total.stripTrailingZeros().toPlainString()} ${limit_quote_spinner.getSelectedSymbol()}"
		} else {
			limit_total?.hint = "You Receive: - ${limit_quote_spinner.getSelectedSymbol()}"
			limit_total?.text = ""
		}
	}
	
	private fun updateAmount(amount: BigDecimal) {
		if (amount > BigDecimal.ZERO) {
			limit_amount_input?.setText(amount.stripTrailingZeros().toPlainString())
			limit_amount_input?.setSelection(limit_amount_input?.text?.length ?: 0)
		} else {
			limit_amount_input?.setText("")
		}
	}
	
	private fun updatePrice(price: BigDecimal) {
		if (price > BigDecimal.ZERO) {
			limit_price_input?.setText(price.toLongDisplayFormat())
			limit_price_input?.setSelection(limit_price_input?.text?.length ?: 0)
		} else {
			limit_price_input?.setText("")
		}
	}
}