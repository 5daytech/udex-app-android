package com.blocksdecoded.dex.utils.ui

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun timestampToDisplay(timestamp: Long): String {
        val date = Date(timestamp * 1000)

        return SimpleDateFormat("dd MMMM hh:mm", Locale.US).format(date)
    }

    fun timestampToShort(timestamp: Long): String {
        val date = Date(timestamp * 1000)

        return SimpleDateFormat("MMM\ndd", Locale.US).format(date)
    }

    fun dateToShort(date: Date): String = SimpleDateFormat("MMM\ndd", Locale.US).format(date)

    fun dateSimpleFormat(date: Date): String {
        return SimpleDateFormat("EEEE, dd", Locale.US).format(date)
    }
}