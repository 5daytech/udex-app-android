package com.blocksdecoded.dex.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blocksdecoded.dex.core.model.EnabledCoin

@Database(
    version = 1,
    exportSchema = false,
    entities = [EnabledCoin::class]
)
abstract class AppDatabase : RoomDatabase() {

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