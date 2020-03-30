package com.fridaytech.dex.data.zrx.model

import com.fridaytech.dex.core.model.Coin
import com.fridaytech.dex.data.manager.rates.RatesConverter
import com.fridaytech.dex.data.zrx.OrdersUtil
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.utils.TimeUtils
import com.fridaytech.zrxkit.model.OrderInfo
import com.fridaytech.zrxkit.relayer.model.OrderRecord
import java.math.BigDecimal

data class SimpleOrder(
    val makerCoin: Coin,
    val takerCoin: Coin,
    val price: BigDecimal,
    val makerAmount: BigDecimal,
    val remainingMakerAmount: BigDecimal,
    val takerAmount: BigDecimal,
    val remainingTakerAmount: BigDecimal,
    val makerFiatAmount: BigDecimal,
    val takerFiatAmount: BigDecimal,
    val expireDate: String,
    val side: EOrderSide,
    val isMine: Boolean,
    val status: String,
    val filledAmount: BigDecimal
) {
    companion object {
        fun fromOrder(
            ratesConverter: RatesConverter,
            orderRecord: OrderRecord,
            side: EOrderSide,
            orderInfo: OrderInfo? = null,
            isMine: Boolean = false
        ): SimpleOrder {
            val normalizedData = OrdersUtil.normalizeOrderData(orderRecord)

            return SimpleOrder(
                normalizedData.makerCoin,
                normalizedData.takerCoin,
                normalizedData.price,
                normalizedData.makerAmount,
                normalizedData.remainingMakerAmount ?: normalizedData.makerAmount,
                normalizedData.takerAmount,
                normalizedData.remainingTakerAmount ?: normalizedData.takerAmount,
                ratesConverter.getCoinsPrice(
                    normalizedData.makerCoin.code,
                    normalizedData.remainingMakerAmount ?: normalizedData.makerAmount
                ),
                ratesConverter.getCoinsPrice(
                    normalizedData.takerCoin.code,
                    normalizedData.remainingTakerAmount ?: normalizedData.takerAmount
                ),
                TimeUtils.timestampToDisplay(orderRecord.order.expirationTimeSeconds.toLong()),
                side,
                isMine,
                orderInfo?.orderStatus ?: "unknown",
                normalizedData.takerAmount - (normalizedData.remainingTakerAmount ?: normalizedData.takerAmount)
            )
        }
    }
}
