package com.blocksdecoded.dex.utils

import android.graphics.PorterDuff
import android.os.Handler
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R

object HudHelper {

    private var toast: Toast? = null

    fun showSuccessMessage(text: Int, durationInMillis: Long = 2000) {
        showHudNotification(text, R.color.green, durationInMillis)
    }

    fun showErrorMessage(text: Int) {
        showHudNotification(text, R.color.red, 2000)
    }

    private fun showHudNotification(text: Int, backgroundColor: Int, durationInMillis: Long) {
        toast?.cancel()

        val toast = Toast.makeText(App.instance, text, Toast.LENGTH_SHORT)

        val toastText = toast.view.findViewById(android.R.id.message) as TextView
        toastText.setTextColor(ContextCompat.getColor(toast.view.context, R.color.white))
        toast.view.background.setColorFilter(ContextCompat.getColor(toast.view.context, backgroundColor), PorterDuff.Mode.SRC_IN)
        toast.setGravity(Gravity.TOP, 0, 120)
        toast.show()

        Handler().postDelayed({ toast.cancel() }, durationInMillis)
    }
}
