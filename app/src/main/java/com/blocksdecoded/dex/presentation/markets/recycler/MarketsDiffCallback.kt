package com.blocksdecoded.dex.presentation.markets.recycler

import androidx.recyclerview.widget.DiffUtil
import com.blocksdecoded.dex.presentation.markets.MarketViewItem

class MarketsDiffCallback(
    private val oldMarkets: List<MarketViewItem>,
    private val newMarkets: List<MarketViewItem>
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

        oldCoin.price == newCoin.price && oldCoin.change == newCoin.change
    } catch (e: Exception) {
        false
    }
}