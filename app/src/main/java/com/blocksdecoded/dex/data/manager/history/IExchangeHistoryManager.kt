package com.blocksdecoded.dex.data.manager.history

import io.reactivex.subjects.BehaviorSubject

interface IExchangeHistoryManager {
    val exchangeHistory: List<ExchangeRecord>
    val syncSubject: BehaviorSubject<Unit>
}