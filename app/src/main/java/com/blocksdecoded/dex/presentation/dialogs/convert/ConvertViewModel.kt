package com.blocksdecoded.dex.presentation.dialogs.convert

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.manager.CoinManager
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.dialogs.convert.ConvertConfig.ConvertType.*
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.subscribeUi
import java.math.BigDecimal

class ConvertViewModel : CoreViewModel() {

    private lateinit var config: ConvertConfig
    private val wethWrapper = App.zrxKitManager.zrxKit().getWethWrapperInstance()
    private var adapter: IAdapter? = null
    
    private lateinit var fromCoin: Coin
    private lateinit var toCoin: Coin
    
	private var sendAmount = BigDecimal.ZERO
	
    var decimalSize: Int = 18
	
    val convertState = MutableLiveData<ConvertState>()
    val amount = MutableLiveData<BigDecimal>()
    val receiveAmount = MutableLiveData<BigDecimal>()
    val convertEnabled = MutableLiveData<Boolean>()
    
    val dismissDialog = SingleLiveEvent<Unit>()
    val transactionSentEvent = SingleLiveEvent<String>()
    
    fun init(config: ConvertConfig) {
        this.config = config
    
        adapter = App.adapterManager.adapters
            .firstOrNull { it.coin.code == config.coinCode }
        
        fromCoin = CoinManager.getCoin(config.coinCode)
        toCoin = CoinManager.getCoin(
            if (config.type == WRAP)
                "WETH"
            else
                "ETH"
        )
        
        convertState.value = ConvertState(
            fromCoin,
            toCoin,
            adapter?.balance ?: BigDecimal.ZERO,
            config.type
        )
        
        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            return
        }
	
	    sendAmount = BigDecimal.ZERO
        onAmountChanged(sendAmount)
	    
        decimalSize = adapter?.decimal ?: 18
    }

    fun onMaxClicked() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        onAmountChanged(availableBalance)
	    refreshAmount()
    }
	
	private fun refreshAmount() {
		this.amount.value = sendAmount
	}
	
	fun onConvertClick() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        if (sendAmount <= availableBalance) {
            messageEvent.postValue(R.string.message_convert_processing)
            val sendRaw = sendAmount.movePointRight(18).stripTrailingZeros().toBigInteger()
            onAmountChanged(BigDecimal.ZERO)
	        refreshAmount()
            
            when(config.type) {
                WRAP -> wethWrapper.deposit(sendRaw)
                UNWRAP -> wethWrapper.withdraw(sendRaw)
            }.subscribeUi(disposables, {
                dismissDialog.call()
                transactionSentEvent.postValue(it.transactionHash)
            }, {
                Logger.e(it)
                errorEvent.postValue(R.string.error_convert_failed)
            })
        } else {
            errorEvent.postValue(R.string.error_invalid_amount)
        }
	}
    
    fun onAmountChanged(amount: BigDecimal?) {
	    sendAmount = amount ?: BigDecimal.ZERO
	    convertEnabled.value = sendAmount > BigDecimal.ZERO
    }
}