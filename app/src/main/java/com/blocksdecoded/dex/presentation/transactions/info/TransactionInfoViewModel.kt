package com.blocksdecoded.dex.presentation.transactions.info

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.transactions.TransactionViewItem

class TransactionInfoViewModel: CoreViewModel() {

    val transactionView = MutableLiveData<TransactionViewItem>()

    val fullInfoEvent = SingleLiveEvent<Unit>()
    val dismissEvent = SingleLiveEvent<Unit>()

    fun init(transactionItem: TransactionViewItem) {
        transactionView.postValue(transactionItem)
    }

    fun onFullInfoClicked() {

    }


}