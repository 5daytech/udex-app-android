package com.fridaytech.dex.utils

import android.util.Log

object Logger {
    private val TAG = "logger"

    fun d(message: String?, tag: String = TAG) {
        if (message != null) {
            Log.d(tag, message)
        }
    }

    fun e(throwable: Throwable, tag: String = TAG) {
        e(throwable.message, throwable, tag)
    }

    fun e(message: String?, throwable: Throwable, tag: String = TAG) {
        Log.e(tag, message, throwable)
    }

    fun w(message: String?) {
        if (message != null) {
            Log.w(TAG, message)
        }
    }
}
