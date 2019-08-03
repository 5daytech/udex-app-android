package com.blocksdecoded.dex.presentation.exchange

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairs
import java.math.BigDecimal

class ExchangeViewModel : CoreViewModel() {

    private val relayer = App.relayerAdapterManager.getMainAdapter()

    val sendAmount = MutableLiveData<BigDecimal>()
    val receiveAmount = MutableLiveData<BigDecimal>()

    val selectedSendCoin = MutableLiveData<ExchangePairItem>()
    val selectedReceiveCoin = MutableLiveData<ExchangePairItem>()

    val exchangePairs = MutableLiveData<ExchangePairs>()

    init {

    }

    fun onPairChange() {

    }

    fun onSendAmountChange(amount: BigDecimal) {

    }

    fun onReceiveAmountChange(amount: BigDecimal) {

    }

    fun onMaxClick() {

    }

    fun onExchangeClick() {

    }

    fun onSwitchClick() {
        val sendCoin = selectedSendCoin.value
        selectedSendCoin.value = selectedReceiveCoin.value
        selectedReceiveCoin.value = sendCoin

        val amount = sendAmount.value
        receiveAmount.value = amount
        sendAmount.value = receiveAmount.value
    }

}
