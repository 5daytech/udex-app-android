package com.blocksdecoded.dex.data.storage

import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.data.storage.dao.RatesDao
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.concurrent.Executors

class RatesStorage(
    private val ratesDao: RatesDao
) : IRatesStorage {
    private val executor = Executors.newSingleThreadExecutor()

    override fun getRate(coinCode: String, timeStamp: Long): Rate =
        ratesDao.getRate(coinCode, timeStamp)

    override fun getLatestRates(): Single<List<Rate>> =
        Maybe.create<List<Rate>> { emitter ->
            val rates = ratesDao.getLatestRates()

            if (rates.isEmpty()) {
                emitter.onComplete()
            } else {
                emitter.onSuccess(rates)
            }
        }.toSingle()

    override fun saveLatest(rates: List<Rate>) {
        executor.execute {
            ratesDao.deleteLatest()
            ratesDao.insert(*rates.toTypedArray())
        }
    }

    override fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate> =
        ratesDao.getRateSingle(coinCode, timeStamp)

    override fun save(vararg rates: Rate) {
        executor.execute { ratesDao.insert(*rates) }
    }

    override fun deleteAll() {
        executor.execute { ratesDao.deleteAll() }
    }
}