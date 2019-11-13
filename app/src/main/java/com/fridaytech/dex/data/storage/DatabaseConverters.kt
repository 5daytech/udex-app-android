package com.fridaytech.dex.data.storage

import androidx.room.TypeConverter
import java.math.BigDecimal

class DatabaseConverters {

    @TypeConverter
    fun fromString(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun toString(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }

    @TypeConverter
    fun stringToFloatList(value: String?): List<Float?>? {
        return value?.split(",")?.map { it.toFloatOrNull() }
    }

    @TypeConverter
    fun floatListToString(value: List<Float>?): String? {
        return value?.joinToString(separator = ",")
    }
}
