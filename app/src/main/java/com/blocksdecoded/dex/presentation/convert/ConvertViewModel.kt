package com.blocksdecoded.dex.presentation.convert

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.convert.ConvertConfig.ConvertType.*
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal

class ConvertViewModel : CoreViewModel() {

    private lateinit var config: ConvertConfig
    private val coinManager = App.coinManager
    private val wethWrapper = App.zrxKitManager.zrxKit().getWethWrapperInstance()
    private val ratesConverter = App.ratesConverter
    private var adapter: IAdapter? = null
    
    private lateinit var fromCoin: Coin
    private lateinit var toCoin: Coin
    
	private var sendAmount = BigDecimal.ZERO
	
    var decimalSize: Int = 18
	
    val convertState = MutableLiveData<ConvertState>()
    val convertAmount = MutableLiveData<BigDecimal>()
    val receiveAmount = MutableLiveData<BigDecimal>()
    val convertEnabled = MutableLiveData<Boolean>()

    val dismissDialog = SingleLiveEvent<Unit>()
    val transactionSentEvent = SingleLiveEvent<String>()
    val processingEvent = SingleLiveEvent<Unit>()
    val dismissProcessingEvent = SingleLiveEvent<Unit>()
    
    fun init(config: ConvertConfig) {
        this.config = config
    
        adapter = App.adapterManager.adapters
            .firstOrNull { it.coin.code == config.coinCode }
    
        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            dismissDialog.call()
            return
        }
        
        fromCoin = coinManager.getCoin(config.coinCode)
        toCoin = coinManager.getCoin(
            if (config.type == WRAP)
                "WETH"
            else
                "ETH"
        )
        
        val balanceInfo = TotalBalanceInfo(
            adapter!!.coin,
            adapter!!.balance,
            ratesConverter.getCoinsPrice(adapter!!.coin.code, adapter!!.balance)
        )
        
        convertState.value = ConvertState(
            fromCoin,
            toCoin,
            balanceInfo,
            config.type
        )
	
	    sendAmount = BigDecimal.ZERO
        onAmountChanged(sendAmount, true)
	    
        decimalSize = adapter?.decimal ?: 18
    }

    fun onMaxClicked() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        onAmountChanged(availableBalance, true)
    }
	
	fun onConvertClick() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        if (sendAmount <= availableBalance) {
            processingEvent.call()
            val sendRaw = sendAmount.movePointRight(18).stripTrailingZeros().toBigInteger()
            onAmountChanged(BigDecimal.ZERO, true)
            
            when(config.type) {
                WRAP -> wethWrapper.deposit(sendRaw)
                UNWRAP -> wethWrapper.withdraw(sendRaw)
            }.uiSubscribe(disposables, {
                dismissProcessingEvent.call()
                transactionSentEvent.postValue(it.transactionHash)
                dismissDialog.call()
            }, {
                Logger.e(it)
                dismissProcessingEvent.call()
                errorEvent.postValue(when(config.type) {
                    WRAP -> R.string.error_wrap_failed
                    UNWRAP -> R.string.error_unwrap_failed
                })
            })
        } else {
            errorEvent.postValue(R.string.error_invalid_amount)
        }
	}
    
    fun onAmountChanged(amount: BigDecimal?, updateLiveData: Boolean = false) {
	    sendAmount = amount ?: BigDecimal.ZERO
	    convertEnabled.value = sendAmount > BigDecimal.ZERO

        if (updateLiveData) {
            this.convertAmount.value = BigDecimal.ZERO
        }
    }
}