package com.fridaytech.dex.data.manager.fee

import com.fridaytech.dex.data.adapter.FeeRatePriority

interface IFeeRateProvider {
    fun ethereumGasPrice(priority: FeeRatePriority): Long
}
