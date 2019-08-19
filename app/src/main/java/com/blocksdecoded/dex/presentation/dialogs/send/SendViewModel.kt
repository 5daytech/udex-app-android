package com.blocksdecoded.dex.presentation.dialogs.send

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.utils.uiObserver
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.clipboard.ClipboardManager
import java.math.BigDecimal

class SendViewModel: CoreViewModel() {
    private lateinit var adapter: IAdapter
    private var userInput = SendUserInput()

    var decimalSize: Int? = null

    val coin = MutableLiveData<Coin>()
    val receiveAddress = MutableLiveData<String>()
    val sendEnabled = MutableLiveData<Boolean>()
    val amount = MutableLiveData<BigDecimal>()
    
    val dismissEvent = SingleLiveEvent<Unit>()
    val dismissWithSuccessEvent = SingleLiveEvent<Unit>()
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
    
        coin.value = adapter.coin
        userInput = SendUserInput()
        sendEnabled.value = false
        decimalSize = adapter.decimal
        amount.value = userInput.amount
        receiveAddress.value = userInput.address ?: ""
    }
    
    private fun refreshSendEnable() {
        sendEnabled.value = userInput.amount > BigDecimal.ZERO &&
            userInput.address != null
    }

    fun onAmountChanged(amount: BigDecimal) {
        if (userInput.amount != amount) {
            userInput.amount = amount
            
            refreshSendEnable()
        }
    }

    fun onBarcodeClick() {
        openBarcodeScannerEvent.call()
    }

    fun onMaxClicked() {
        val balance = adapter.availableBalance(adapter.receiveAddress, FeeRatePriority.HIGHEST)
        onAmountChanged(balance)
        amount.value = balance
    }

    fun onSwitchClick() {

    }

    fun onPasteClick() {
        userInput.address = ClipboardManager.getCopiedText()
        receiveAddress.value = userInput.address
        refreshSendEnable()
    }

    fun onDeleteAddressClick() {
        receiveAddress.value = ""
        userInput.address = null
	    refreshSendEnable()
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
                .uiObserver()
                .subscribe({
                    dismissWithSuccessEvent.call()
                }, {
                    Logger.e(it)
                    messageEvent.postValue(R.string.error_send)
                }).let { disposables.add(it) }
    }
}