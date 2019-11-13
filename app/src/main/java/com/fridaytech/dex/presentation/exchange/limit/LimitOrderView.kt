package com.fridaytech.dex.presentation.exchange.limit

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputConnection
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.exchange.model.ExchangeAmountInfo
import com.fridaytech.dex.presentation.exchange.model.ExchangePairsInfo
import com.fridaytech.dex.presentation.exchange.model.LimitOrderViewState
import com.fridaytech.dex.presentation.models.AmountInfo
import com.fridaytech.dex.utils.getAttr
import com.fridaytech.dex.utils.ui.AnimationHelper
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.fridaytech.dex.utils.ui.toLongDisplayFormat
import com.fridaytech.dex.utils.ui.toPriceFormat
import com.fridaytech.dex.utils.visible
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import kotlinx.android.synthetic.main.view_limit_order.view.*

class LimitOrderView : CardView {
    init { View.inflate(context, R.layout.view_limit_order, this) }

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private var state: LimitOrderViewState? = null

    var amountInputConnection: InputConnection? = null
    var priceInputConnection: InputConnection? = null

    val amountChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()
    val priceChangeSubject: PublishSubject<BigDecimal> = PublishSubject.create()

    fun init() {
        amountInputConnection = limit_amount_input?.bind { amount ->
            limit_amount_max?.visible = amount <= BigDecimal.ZERO

            amountChangeSubject.onNext(amount)
        }

        priceInputConnection = limit_price_input?.bind { price ->
            priceChangeSubject.onNext(price)
        }
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

    fun updateTotal(totalInfo: ExchangeAmountInfo) {
        updateTotal(totalInfo.amount)
    }

    fun updatePrice(priceInfo: AmountInfo) {
        updatePrice(priceInfo.value)
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

    fun updateSendInfo(info: AmountInfo) {
        val hintColor = context.theme.getAttr(R.attr.SecondaryHintTextColor) ?: 0
        val errorColor = ContextCompat.getColor(context, R.color.red)

        val hintInputColor = if (info.error == 0) hintColor else errorColor

        limit_amount_hint?.setTextColor(hintInputColor)

        if (info.error == 0) {
            limit_amount_hint?.text = context.getString(R.string.hint_i_have, info.value.toFiatDisplayFormat())
        } else {
            limit_amount_hint?.setText(info.error)
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
            limit_price_input?.setText(price.stripTrailingZeros().toLongDisplayFormat())
            limit_price_input?.setSelection(limit_price_input?.text?.length ?: 0)
        } else {
            limit_price_input?.setText("")
        }
    }
}
