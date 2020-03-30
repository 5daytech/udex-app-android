package com.fridaytech.dex.data.zrx

import com.fridaytech.dex.App
import com.fridaytech.dex.core.model.CoinType
import com.fridaytech.dex.data.zrx.model.NormalizedOrderData
import com.fridaytech.dex.presentation.orders.model.EOrderSide
import com.fridaytech.dex.utils.normalizedDiv
import com.fridaytech.zrxkit.model.EAssetProxyId
import com.fridaytech.zrxkit.model.IOrder
import com.fridaytech.zrxkit.model.SignedOrder
import com.fridaytech.zrxkit.relayer.model.OrderRecord
import java.math.BigDecimal

object OrdersUtil {
    val coinManager = App.coinManager

    private fun getErcCoin(coinCode: String): CoinType.Erc20 =
        coinManager.getCoin(coinCode).type as CoinType.Erc20

    fun normalizeOrderData(orderRecord: OrderRecord): NormalizedOrderData {
        val makerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(orderRecord.order.makerAssetData))!!
        val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(orderRecord.order.takerAssetData))!!

        val makerAmount = orderRecord.order.makerAssetAmount.toBigDecimal()
            .movePointLeft((makerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val takerAmount = orderRecord.order.takerAssetAmount.toBigDecimal()
            .movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val remainingTakerAmount = orderRecord.metaData?.remainingFillableTakerAssetAmount?.toBigDecimal()
            ?.movePointLeft(takerCoin.type.decimal)
            ?.stripTrailingZeros()

        val remainingMakerAmount = remainingTakerAmount?.let {
            if (remainingTakerAmount > BigDecimal.ZERO) {
                makerAmount * remainingTakerAmount.normalizedDiv(takerAmount)
            } else {
                BigDecimal.ZERO
            }
        }

        val price = takerAmount.normalizedDiv(makerAmount)

        return NormalizedOrderData(
            "",
            makerCoin,
            takerCoin,
            makerAmount,
            remainingMakerAmount,
            takerAmount,
            remainingTakerAmount,
            price,
            orderRecord.order
        )
    }

    fun normalizeOrderDataPrice(orderRecord: OrderRecord, isSellPrice: Boolean = true): NormalizedOrderData {
        val makerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(orderRecord.order.makerAssetData))!!
        val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(orderRecord.order.takerAssetData))!!

        val makerAmount = orderRecord.order.makerAssetAmount.toBigDecimal()
            .movePointLeft((makerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val takerAmount = orderRecord.order.takerAssetAmount.toBigDecimal()
            .movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val remainingTakerAmount = orderRecord.metaData?.remainingFillableTakerAssetAmount?.toBigDecimal()
            ?.movePointLeft(takerCoin.type.decimal)
            ?.stripTrailingZeros()

        val remainingMakerAmount = remainingTakerAmount?.let {
            if (remainingTakerAmount > BigDecimal.ZERO) {
                makerAmount * remainingTakerAmount.normalizedDiv(takerAmount)
            } else {
                BigDecimal.ZERO
            }
        }

        val price = if (isSellPrice) {
            makerAmount.normalizedDiv(takerAmount)
        } else {
            takerAmount.normalizedDiv(makerAmount)
        }

        return NormalizedOrderData(
            orderRecord.metaData?.orderHash ?: "",
            makerCoin,
            takerCoin,
            makerAmount,
            remainingMakerAmount,
            takerAmount,
            remainingTakerAmount,
            price,
            orderRecord.order
        )
    }

    fun calculateBasePrice(orders: List<SignedOrder>, coinPair: Pair<String, String>, side: EOrderSide): BigDecimal =
        calculateOrderPrice(coinPair, orders.first(), side)

    fun calculateOrderPrice(coinPair: Pair<String, String>, order: IOrder, side: EOrderSide): BigDecimal {
        val baseCoin = getErcCoin(coinPair.first)
        val quoteCoin = getErcCoin(coinPair.second)

        val makerAmount = order.makerAssetAmount.toBigDecimal()
            .movePointLeft(if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal)
            .stripTrailingZeros()

        val takerAmount = order.takerAssetAmount.toBigDecimal()
            .movePointLeft(if (side == EOrderSide.BUY) baseCoin.decimal else quoteCoin.decimal)
            .stripTrailingZeros()

        return makerAmount.normalizedDiv(takerAmount)
    }
}
