package com.blocksdecoded.dex.core.manager.history

import io.reactivex.subjects.BehaviorSubject

interface IExchangeHistoryManager {
    val exchangeHistory: List<ExchangeRecord>
    val syncSubject: BehaviorSubject<Unit>
}