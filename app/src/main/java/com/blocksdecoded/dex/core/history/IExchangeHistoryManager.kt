package com.blocksdecoded.dex.core.history

import io.reactivex.subjects.BehaviorSubject

interface IExchangeHistoryManager {
    val exchangeHistory: List<ExchangeRecord>
    val syncSubject: BehaviorSubject<Unit>
}