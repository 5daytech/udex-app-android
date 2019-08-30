package com.blocksdecoded.dex.presentation.markets.recycler

import androidx.recyclerview.widget.DiffUtil
import com.blocksdecoded.dex.core.model.Market

class MarketsDiffCallback(
    private val oldMarkets: List<Market>,
    private val newMarkets: List<Market>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = try {
        oldMarkets[oldItemPosition].coin.code == newMarkets[newItemPosition].coin.code
    } catch (e: Exception) {
        false
    }

    override fun getOldListSize(): Int = oldMarkets.size

    override fun getNewListSize(): Int = newMarkets.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = try {
        val oldCoin = oldMarkets[oldItemPosition]
        val newCoin = newMarkets[newItemPosition]

        oldCoin.rate.price == newCoin.rate.price && oldCoin.rate.priceChange == newCoin.rate.priceChange
    } catch (e: Exception) {
        false
    }
}