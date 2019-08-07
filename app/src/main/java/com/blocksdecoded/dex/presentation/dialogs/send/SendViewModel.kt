package com.blocksdecoded.dex.presentation.dialogs.send

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.utils.observeUi
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.clipboard.ClipboardManager
import java.math.BigDecimal

class SendViewModel: CoreViewModel() {
    private lateinit var adapter: IAdapter
    private var userInput = SendUserInput()

    var decimalSize: Int? = null

    val receiveAddress = MutableLiveData<String>()
    val sendEnabled = MutableLiveData<Boolean>()
    val amount = MutableLiveData<BigDecimal>()

    val dismissEvent = SingleLiveEvent<Unit>()
    val dismissWithSuccessEvent = SingleLiveEvent<Unit>()
    val messageEvent = SingleLiveEvent<Int>()
    val openBarcodeScannerEvent = SingleLiveEvent<Unit>()

    fun init(coinCode: String) {
        val adapter = App.adapterManager.adapters
                .firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            dismissEvent.call()
            return
        } else {
            this.adapter = adapter
        }

        sendEnabled.value = false
        decimalSize = adapter.decimal
        amount.value = BigDecimal.ZERO
    }

    fun onAmountChanged(amount: BigDecimal) {
        if (userInput.amount != amount) {
            userInput.amount = amount
    
            this.amount.value = amount
    
            sendEnabled.value = amount > BigDecimal.ZERO && userInput.address != null
        }
    }

    fun onBarcodeClick() {
        openBarcodeScannerEvent.call()
    }

    fun onMaxClicked() {

    }

    fun onSwitchClick() {

    }

    fun onPasteClick() {
        receiveAddress.value = ClipboardManager.getCopiedText()
        userInput.address = receiveAddress.value
    }

    fun onDeleteAddressClick() {
        receiveAddress.value = ""
        userInput.address = null
    }

    fun onSendClicked() {
        send(userInput)
    }

    private fun send(userInput: SendUserInput) {
        val address = userInput.address
        if (address == null) {
            //TODO: Show empty address error
            return
        }

        val amount = userInput.amount
        if (amount == BigDecimal.ZERO) {
            //TODO: Show no amount error
            return
        }

        adapter.send(address, amount, userInput.feePriority)
                .observeUi()
                .subscribe({
                    dismissWithSuccessEvent.call()
                }, {
                    Logger.e(it)
                    messageEvent.postValue(R.string.error_send)
                }).let { disposables.add(it) }
    }
}