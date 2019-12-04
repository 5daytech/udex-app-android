package com.fridaytech.dex.data.manager.fee

import android.content.Context
import com.fridaytech.dex.core.IAppConfiguration
import com.fridaytech.dex.data.adapter.FeeRatePriority
import com.fridaytech.dex.data.adapter.FeeRatePriority.*
import io.horizontalsystems.feeratekit.Coin
import io.horizontalsystems.feeratekit.FeeRate
import io.horizontalsystems.feeratekit.FeeRateKit
import java.util.*

class FeeRateProvider(
    context: Context,
    appConfiguration: IAppConfiguration
) : IFeeRateProvider, FeeRateKit.Listener {

    private val feeRateKit = FeeRateKit(
        appConfiguration.infuraProjectId,
        appConfiguration.infuraProjectSecret,
        context,
        this
    ).apply { refresh() }

    private val etherFeeRates = FeeRate(
        Coin.ETHEREUM,
        4_000_000_000,
        60 * 30,
        8_000_000_000,
        60 * 5,
        20_000_000_000,
        60 * 2,
        date = Date().time)

    override fun ethereumGasPrice(priority: FeeRatePriority): Long = feeRate(etherFeeRates, priority)

    override fun onRefresh(rates: List<FeeRate>) {

    }

    private fun feeRate(feeRate: FeeRate, priority: FeeRatePriority): Long = when (priority) {
        LOWEST -> feeRate.lowPriority
        LOW -> (feeRate.lowPriority + feeRate.mediumPriority) / 2
        MEDIUM -> feeRate.mediumPriority
        HIGH -> (feeRate.mediumPriority + feeRate.highPriority) / 2
        HIGHEST -> feeRate.highPriority
    }
}
