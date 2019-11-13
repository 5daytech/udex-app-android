package com.fridaytech.dex.utils.ui

import android.app.Activity
import androidx.core.app.ShareCompat

object ShareUtils {
    fun shareMessage(activity: Activity?, message: String) {
        ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setText(message)
            .startChooser()
    }
}
