package com.blocksdecoded.dex.data.storage.dao

import androidx.room.*
import com.blocksdecoded.dex.core.model.Rate
import io.reactivex.Single

@Dao
interface RatesDao {
    @Query("SELECT * FROM Rate")
    fun getRates(): List<Rate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg rates: Rate)

    @Query("SELECT * FROM Rate WHERE isLatest = 1")
    fun getLatestRates(): List<Rate>

    @Query("DELETE FROM Rate WHERE isLatest = 1")
    fun deleteLatest()

    @Delete
    fun delete(rate: Rate)

    @Query("DELETE FROM Rate")
    fun deleteAll()

    @Query("SELECT * FROM Rate WHERE coinCode = :coinCode AND timestamp = :timestamp")
    fun getRateSingle(coinCode: String, timestamp: Long): Single<Rate>

    @Query("SELECT * FROM Rate WHERE coinCode = :coinCode AND timestamp = :timestamp")
    fun getRate(coinCode: String, timestamp: Long): Rate
}
