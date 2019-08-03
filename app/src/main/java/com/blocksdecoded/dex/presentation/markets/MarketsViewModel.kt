package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.ui.CoreViewModel

class MarketsViewModel : CoreViewModel() {
    val markets = MutableLiveData<List<Market>>()
}
