package com.blocksdecoded.dex.core.tradehistory

import io.reactivex.subjects.BehaviorSubject

interface ITradeHistoryManager {
    val tradesHistory: List<TradeRecord>
    val tradesUpdateSubject: BehaviorSubject<Unit>
}