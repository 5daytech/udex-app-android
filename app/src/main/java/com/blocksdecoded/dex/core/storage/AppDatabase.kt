package com.blocksdecoded.dex.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blocksdecoded.dex.core.model.EnabledCoin
import com.blocksdecoded.dex.core.model.Market
import com.blocksdecoded.dex.core.model.Rate
import com.blocksdecoded.dex.core.storage.dao.EnabledCoinsDao
import com.blocksdecoded.dex.core.storage.dao.MarketsDao
import com.blocksdecoded.dex.core.storage.dao.RatesDao

@Database(
    version = 3,
    exportSchema = false,
    entities = [Market::class, EnabledCoin::class, Rate::class]
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketsDao(): MarketsDao

    abstract fun ratesDao(): RatesDao

    abstract fun enabledCoinsDao(): EnabledCoinsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "dexDb")
                .fallbackToDestructiveMigration()
                .build()
    }
}