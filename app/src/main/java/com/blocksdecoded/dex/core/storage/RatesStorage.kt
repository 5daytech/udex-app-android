package com.blocksdecoded.dex.core.storage

import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.manager.rates.IRatesStorage
import com.blocksdecoded.dex.core.storage.dao.RatesDao
import io.reactivex.Single
import java.util.concurrent.Executors

class RatesStorage(
    private val ratesDao: RatesDao
) : IRatesStorage {
    private val executor = Executors.newSingleThreadExecutor()

    override fun getRate(coinCode: String, timeStamp: Long): Rate =
        ratesDao.getRate(coinCode, timeStamp)

    override fun getRateSingle(coinCode: String, timeStamp: Long): Single<Rate> =
        ratesDao.getRateSingle(coinCode, timeStamp)

    override fun save(vararg rates: Rate) {
        executor.execute { ratesDao.insert(*rates) }
    }

    override fun deleteAll() {
        executor.execute { ratesDao.deleteAll() }
    }
}