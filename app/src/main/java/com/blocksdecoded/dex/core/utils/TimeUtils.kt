package com.blocksdecoded.dex.core.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun timestampToDisplay(timestamp: String): String {
        val date = Date(timestamp.toLong() * 1000)

        return SimpleDateFormat("dd/MM/yy hh:mm", Locale.US).format(date)
    }
}