package com.blocksdecoded.dex.core.storage

import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.rates.IRatesStorage
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.concurrent.Executors

class RatesStorage(
    private val ratesDao: RatesDao
) : IRatesStorage {
    private val executor = Executors.newSingleThreadExecutor()

    override fun allRates(): Single<List<Rate>> = Maybe.create<List<Rate>> { emitter ->
        val rates = ratesDao.getRates()

        if (rates.isEmpty()) {
            emitter.onComplete()
        } else {
            emitter.onSuccess(rates)
        }
    }.toSingle()

    override fun rateSingle(symbol: String): Single<Rate> = ratesDao.getRate(symbol)

    override fun save(vararg rates: Rate) = executor.execute { ratesDao.insert(*rates) }

    override fun deleteAll() = executor.execute { ratesDao.deleteAll() }
}