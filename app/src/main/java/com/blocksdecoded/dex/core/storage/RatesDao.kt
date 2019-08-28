package com.blocksdecoded.dex.core.storage

import androidx.room.*
import com.blocksdecoded.dex.core.model.Rate
import io.reactivex.Single

@Dao
interface RatesDao {

    @Query("SELECT * FROM Rate")
    fun getRates(): List<Rate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg rate: Rate)

    @Delete
    fun delete(rate: Rate)

    @Query("DELETE FROM Rate")
    fun deleteAll()

    @Query("SELECT * FROM Rate WHERE symbol = :symbol")
    fun getRate(symbol: String): Single<Rate>

}