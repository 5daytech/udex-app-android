package com.fridaytech.dex.presentation.exchange.confirm

import androidx.lifecycle.MutableLiveData
import com.fridaytech.dex.App
import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.core.ui.CoreViewModel
import com.fridaytech.dex.core.ui.SingleLiveEvent
import com.fridaytech.dex.data.manager.duration.ETransactionType
import com.fridaytech.dex.presentation.model.FeeInfo
import com.fridaytech.dex.utils.normalizedDiv
import java.math.BigDecimal

class ExchangeConfirmViewModel : CoreViewModel() {

    private val processingDurationProvider = App.processingDurationProvider
    private val adapterManager = App.adapterManager
    private val coinManager = App.coinManager
    private var confirmInfo: ExchangeConfirmInfo? = null

    val viewState = MutableLiveData<ViewState>()
    val feeInfo = MutableLiveData<FeeInfo>()
    val processingTime = MutableLiveData<Long>()
    val dismissEvent = SingleLiveEvent<Unit>()

    fun init(info: ExchangeConfirmInfo) {
        confirmInfo = info

        val sendAdapter = adapterManager.adapters.firstOrNull { it.coin.code == info.sendCoin }
        val sendCoin = coinManager.getCoin(info.sendCoin)

        val price = info.receiveAmount.normalizedDiv(info.sendAmount)

        val state = ViewState(
            sendCoin,
            coinManager.getCoin(info.receiveCoin),
            info.sendAmount,
            info.receiveAmount,
            price
        )

        viewState.value = state

        feeInfo.value = FeeInfo(
            sendAdapter?.feeCoinCode ?: "",
            info.fee,
            BigDecimal.ZERO,
            0
        )

        processingTime.value = processingDurationProvider.getEstimatedDuration(sendCoin, ETransactionType.EXCHANGE)
    }

    fun onConfirmClick() {
        confirmInfo?.onConfirm?.invoke()
        dismissEvent.call()
    }

    data class ViewState(
        val fromCoin: Coin,
        val toCoin: Coin,
        val sendAmount: BigDecimal,
        val receiveAmount: BigDecimal,
        val price: BigDecimal
    )
}
