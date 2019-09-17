package com.blocksdecoded.dex.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    const val INPUT_HANDLE_DELAY = 150L

    fun getSecondsAgo(dateInMillis: Long): Long {
        val differenceInMillis = Date().time - dateInMillis
        return differenceInMillis / 1000
    }

    fun dateInUTC(timestamp: Long, dateFormat: String): String {
        val dateFormatter = SimpleDateFormat(dateFormat, Locale("US"))
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormatter.format(Date(timestamp * 1000)) //timestamp in seconds
    }

    fun timestampToDisplay(timestamp: Long): String {
        val date = Date(timestamp * 1000)

        return SimpleDateFormat("dd MMMM hh:mm", Locale.US).format(date)
    }

    fun timestampToDisplayFormat(timestamp: Long): String =
        dateToDisplayFormat(Date(timestamp * 1000))

    fun dateToDisplayFormat(date: Date): String {
        return SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.US).format(date)
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