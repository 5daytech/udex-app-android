package com.blocksdecoded.dex.core.manager.fee

import com.blocksdecoded.dex.core.adapter.FeeRatePriority

interface IFeeRateProvider {
    fun ethereumGasPrice(priority: FeeRatePriority): Long
}