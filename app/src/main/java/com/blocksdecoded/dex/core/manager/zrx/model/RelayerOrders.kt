package com.blocksdecoded.dex.core.manager.zrx.model

import io.reactivex.subjects.BehaviorSubject

class RelayerOrdersList<T> {
    private val allOrders: ArrayList<RelayerOrders<T>> = ArrayList()
    val pairUpdateSubject = BehaviorSubject.create<RelayerOrders<T>>()

    fun getPair(baseAsset: String, quoteAsset: String): RelayerOrders<T> = allOrders.firstOrNull {
        try {
            it.baseAsset == baseAsset && it.quoteAsset == quoteAsset
        } catch (e: Exception) {
            false
        }
    } ?: RelayerOrders(baseAsset, quoteAsset, listOf())

    fun updatePairOrders(baseAsset: String, quoteAsset: String, orders: List<T>) {
        val index = allOrders.indexOfFirst { it.baseAsset == baseAsset && it.quoteAsset == quoteAsset }

        val newPair = RelayerOrders(
            baseAsset,
            quoteAsset,
            orders
        )

        if (index >= 0) {
            allOrders[index] = newPair
        } else {
            allOrders.add(newPair)
        }

        pairUpdateSubject.onNext(newPair)
    }

    fun clear() {
        allOrders.clear()
    }
}

data class RelayerOrders<T> (
    val baseAsset: String,
    val quoteAsset: String,
    val orders: List<T>
)