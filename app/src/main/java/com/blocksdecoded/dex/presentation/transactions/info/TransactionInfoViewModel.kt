package com.blocksdecoded.dex.presentation.transactions.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import java.math.BigDecimal
import java.util.*

class TransactionInfoViewModel: CoreViewModel() {

    private val adapterManager = App.adapterManager
    private val ratesManager = App.ratesManager

    val transactionView = MutableLiveData<TransactionViewData>()

    val fullInfoEvent = SingleLiveEvent<Unit>()
    val dismissEvent = SingleLiveEvent<Unit>()

    fun init(transactionInfo: TransactionInfo) {
        adapterManager.adapters
            .firstOrNull {
                it.coin.code == transactionInfo.coin.code
            }?.let {
                val rate = ratesManager.getRate(it.coin.code)

                transactionView.postValue(
                    TransactionViewData(
                        transactionInfo.transactionRecord.transactionHash,
                        transactionInfo.coin,
                        transactionInfo.transactionRecord.amount,
                        BigDecimal.ZERO,
                        transactionInfo.transactionRecord.from.first().address,
                        transactionInfo.transactionRecord.to.first().address,
                        transactionInfo.transactionRecord.to.first().address == it.receiveAddress,
                        Date(transactionInfo.transactionRecord.timestamp),
                        TransactionStatus.Completed,
                        rate
                )
                )
            }
    }

    fun onFullInfoClicked() {

    }


}