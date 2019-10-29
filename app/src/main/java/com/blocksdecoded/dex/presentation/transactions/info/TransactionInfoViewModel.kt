package com.blocksdecoded.dex.presentation.transactions.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.transactions.model.TransactionViewItem

class TransactionInfoViewModel : CoreViewModel() {

    val transactionView = MutableLiveData<TransactionViewItem>()

    val fullInfoEvent = SingleLiveEvent<String>()
    val dismissEvent = SingleLiveEvent<Unit>()

    fun init(transactionItem: TransactionViewItem) {
        transactionView.postValue(transactionItem)
    }

    fun onFullInfoClicked() {
        transactionView.value?.let {
            fullInfoEvent.value = it.transactionHash
        }
    }
}
