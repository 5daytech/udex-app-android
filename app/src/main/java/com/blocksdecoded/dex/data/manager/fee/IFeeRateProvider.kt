package com.blocksdecoded.dex.data.manager.fee

import com.blocksdecoded.dex.data.adapter.FeeRatePriority

interface IFeeRateProvider {
    fun ethereumGasPrice(priority: FeeRatePriority): Long
}
