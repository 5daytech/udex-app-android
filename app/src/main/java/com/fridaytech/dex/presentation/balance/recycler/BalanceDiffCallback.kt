package com.fridaytech.dex.presentation.balance.recycler

import androidx.recyclerview.widget.DiffUtil
import com.fridaytech.dex.core.model.CoinBalance

class BalanceDiffCallback(
    private val oldCoins: List<CoinBalance>,
    private val newCoins: List<CoinBalance>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = try {
        oldCoins[oldItemPosition].coin.code == newCoins[newItemPosition].coin.code
    } catch (e: Exception) {
        false
    }

    override fun getOldListSize(): Int = oldCoins.size

    override fun getNewListSize(): Int = newCoins.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = try {
        val oldCoin = oldCoins[oldItemPosition]
        val newCoin = newCoins[oldItemPosition]

        oldCoin.fiatBalance == newCoin.fiatBalance && oldCoin.balance == newCoin.balance &&
                oldCoin.pricePerToken == newCoin.pricePerToken &&
                oldCoin.convertType == newCoin.convertType &&
                oldCoin.state == newCoin.state
    } catch (e: Exception) {
        false
    }
}
