package com.blocksdecoded.dex.data.storage.dao

import androidx.room.*
import com.blocksdecoded.dex.core.model.Market
import io.reactivex.Single

@Dao
interface MarketsDao {

    @Query("SELECT * FROM Market")
    fun getMarkets(): List<Market>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg market: Market)

    @Delete
    fun delete(market: Market)

    @Query("DELETE FROM Market")
    fun deleteAll()

    @Query("SELECT * FROM Market WHERE coinCode = :coinCode")
    fun getMarket(coinCode: String): Single<Market>
}
