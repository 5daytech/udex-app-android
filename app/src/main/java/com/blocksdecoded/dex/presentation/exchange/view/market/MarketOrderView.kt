package com.blocksdecoded.dex.presentation.exchange.view.market

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.exchange.model.ExchangeAmountInfo
import com.blocksdecoded.dex.presentation.exchange.model.ExchangePairsInfo
import com.blocksdecoded.dex.presentation.exchange.model.MarketOrderViewState
import com.blocksdecoded.dex.presentation.models.AmountInfo
import com.blocksdecoded.dex.utils.getAttr
import com.blocksdecoded.dex.utils.ui.AnimationHelper
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_market_order.view.*
import java.math.BigDecimal

class MarketOrderView: CardView {
	init { View.inflate(context, R.layout.view_market_order, this) }
	
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  { init() }
	
	var sendInputConnection: InputConnection? = null
	var receiveInputConnection: InputConnection? = null

	val sendAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
	val receiveAmountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()

	private fun init() {
		sendInputConnection = market_amount_input?.bind { sendAmount ->
			market_amount_max?.visible = sendAmount <= BigDecimal.ZERO

			sendAmountChangeSubject.onNext(sendAmount)
		}

		receiveInputConnection = market_receive_input?.bind { receiveAmount ->
			receiveAmountChangeSubject.onNext(receiveAmount)
		}
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

	fun updateReceiveInfo(receiveInfo: ExchangeAmountInfo) {
		updateReceiveAmount(receiveInfo.amount)
	}

	fun updateSendInfo(info: ExchangeAmountInfo) {
		updateAmount(info.amount)
	}
	
	@SuppressLint("SetTextI18n")
	fun updateState(state: MarketOrderViewState) {
		updateAmount(state.sendAmount)
		
		updateReceiveAmount(state.receiveAmount)
		
		market_base_spinner?.setSelectedPair(state.sendCoin)
		market_quote_spinner?.setSelectedPair(state.receiveCoin)
	}

	fun updateSendInfo(info: AmountInfo) {
		val hintColor = context.theme.getAttr(R.attr.SecondaryHintTextColor) ?: 0
		val errorColor = ContextCompat.getColor(context, R.color.red)

		val hintInputColor = if (info.error == 0) hintColor else errorColor

		market_amount_hint?.setTextColor(hintInputColor)

		if (info.error == 0) {
			market_amount_hint?.text = context.getString(R.string.hint_i_have, info.value.toFiatDisplayFormat())
		} else {
			market_amount_hint?.setText(info.error)
		}
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
			amount.stripTrailingZeros().toPlainString()
		} else {
			""
		}

		market_receive_input?.setText(text, TextView.BufferType.EDITABLE)
		market_receive_input?.setSelection(market_receive_input?.text?.length ?: 0)
	}
}