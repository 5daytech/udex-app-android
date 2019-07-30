package com.blocksdecoded.dex.core.manager

import android.content.Context
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import io.horizontalsystems.feeratekit.Coin
import io.horizontalsystems.feeratekit.FeeRate
import io.horizontalsystems.feeratekit.FeeRateKit
import java.util.*

class FeeRateProvider(context: Context) : IFeeRateProvider, FeeRateKit.Listener {

    private val feeRateKit = FeeRateKit(context, this).apply { refresh() }

    private val etherFeeRates = FeeRate(
        Coin.ETHEREUM,
        3_000_000_000,
        5_000_000_000,
        10_000_000_000,
        Date().time)

//    override fun ethereumGasPrice(priority: FeeRatePriority): Long = feeRate(feeRateKit.ethereum(), priority)

    override fun ethereumGasPrice(priority: FeeRatePriority): Long = feeRate(etherFeeRates, priority)

    override fun onRefresh(rates: List<FeeRate>) {

    }

    private fun feeRate(feeRate: FeeRate, priority: FeeRatePriority): Long = when (priority) {
        FeeRatePriority.LOWEST -> feeRate.lowPriority
        FeeRatePriority.LOW -> (feeRate.lowPriority + feeRate.mediumPriority) / 2
        FeeRatePriority.MEDIUM -> feeRate.mediumPriority
        FeeRatePriority.HIGH -> (feeRate.mediumPriority + feeRate.highPriority) / 2
        FeeRatePriority.HIGHEST -> feeRate.highPriority
    }
}
