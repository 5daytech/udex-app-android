package com.fridaytech.dex.data.storage.dao

import androidx.room.*
import com.fridaytech.dex.core.model.EnabledCoin
import io.reactivex.Flowable

@Dao
interface EnabledCoinsDao {

    @Query("SELECT * FROM EnabledCoin ORDER BY `order` ASC")
    fun getEnabledCoins(): Flowable<List<EnabledCoin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(enabledCoin: EnabledCoin)

    @Query("DELETE FROM EnabledCoin")
    fun deleteAll()

    @Transaction
    fun insertCoins(coins: List<EnabledCoin>) {
        coins.forEach { insert(it) }
    }
}
