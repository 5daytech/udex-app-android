package com.blocksdecoded.dex.core.adapter

import com.blocksdecoded.dex.core.manager.IFeeRateProvider
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.utils.Logger
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

abstract class EthereumBaseAdapter(
    override val coin: Coin,
    protected val ethereumKit: EthereumKit,
    protected val feeRateProvider: IFeeRateProvider,
    final override val decimal: Int
) : IAdapter {
    override val feeCoinCode: String? = "ETH"

    override val confirmationsThreshold: Int = 12

    override fun start() {
        // started via EthereumKitManager
    }

    override fun stop() {
        // stopped via EthereumKitManager
    }

    override fun refresh() {
        // refreshed via EthereumKitManager
    }

    override val lastBlockHeight: Int? get() = ethereumKit.lastBlockHeight?.toInt()

    override val lastBlockHeightUpdatedFlowable: Flowable<Unit>
        get() = ethereumKit.lastBlockHeightFlowable.map { Unit }

    override fun send(address: String, value: BigDecimal, feePriority: FeeRatePriority): Single<Unit> {
        val poweredDecimal = value.scaleByPowerOfTen(decimal)
        val noScaleDecimal = poweredDecimal.setScale(0, RoundingMode.HALF_DOWN)

        return sendSingle(address, noScaleDecimal.toPlainString(), feeRateProvider.ethereumGasPrice(feePriority))
    }

    override fun validate(address: String) {
        ethereumKit.validateAddress(address)
    }

    override val receiveAddress: String get() = ethereumKit.receiveAddress

    override val debugInfo: String = ethereumKit.debugInfo()

    protected fun balanceInBigDecimal(balance: BigInteger?, decimal: Int): BigDecimal {
        balance?.toBigDecimal()?.let {
            val converted = it.movePointLeft(decimal)
            return converted.stripTrailingZeros()
        } ?: return BigDecimal.ZERO
    }

    open fun sendSingle(address: String, amount: String, gasPrice: Long): Single<Unit> {
        return Single.just(Unit)
    }

}
