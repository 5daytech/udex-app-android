package com.blocksdecoded.dex.presentation.receive

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent

class ReceiveViewModel: CoreViewModel() {
    val title = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val shareEvent = SingleLiveEvent<String>()

    fun init(coinCode: String) {
        App.adapterManager
                .adapters
                .firstOrNull { it.coin.code == coinCode }
    }
}