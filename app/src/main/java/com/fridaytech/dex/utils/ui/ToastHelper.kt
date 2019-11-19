package com.fridaytech.dex.utils.ui

import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fridaytech.dex.App
import com.fridaytech.dex.R

object ToastHelper {

    private var toast: Toast? = null

    fun showInfoMessage(text: Int, durationInMillis: Long = 2000) {
        showMessage(text, R.color.blue, durationInMillis)
    }

    fun showInfoMessage(text: String, durationInMillis: Long = 2000) {
        showMessage(text, R.color.blue, durationInMillis)
    }

    fun showSuccessMessage(text: Int, durationInMillis: Long = 2000) {
        showMessage(text, R.color.dark_green, durationInMillis)
    }

    fun showErrorMessage(text: Int) {
        showMessage(text, R.color.red, 2000)
    }

    private fun showMessage(text: Int, backgroundColor: Int, durationInMillis: Long) {
        toast?.cancel()

        val toast = Toast.makeText(App.instance, text, Toast.LENGTH_SHORT)

//        val toastText = toast.view.findViewById(android.R.id.message) as TextView

        val inflater = App.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customToast = inflater.inflate(R.layout.view_toast, null) as ViewGroup
        toast.view = customToast

        val tv = customToast.findViewById<TextView>(R.id.toast_text)
        tv.setText(text)
        tv.background.setTint(ContextCompat.getColor(toast.view.context, backgroundColor))

//        toastText.setTextColor(ContextCompat.getColor(toast.view.context, R.color.white))
//        toast.view.background.setColorFilter(ContextCompat.getColor(toast.view.context, backgroundColor), PorterDuff.Mode.SRC_IN)

        toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 120)
        toast.show()

        Handler().postDelayed({ toast.cancel() }, durationInMillis)
    }

    private fun showMessage(text: String, backgroundColor: Int, durationInMillis: Long) {
        toast?.cancel()

        val toast = Toast.makeText(App.instance, text, Toast.LENGTH_SHORT)

        val inflater = App.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customToast = inflater.inflate(R.layout.view_toast, null) as ViewGroup
        toast.view = customToast

        val tv = customToast.findViewById<TextView>(R.id.toast_text)
        tv.text = text
        tv.background.setTint(ContextCompat.getColor(toast.view.context, backgroundColor))

//        val toastText = toast.view.findViewById(android.R.id.message) as TextView
//        toastText.setTextColor(ContextCompat.getColor(toast.view.context, R.color.white))
//        toast.view.background.setColorFilter(, PorterDuff.Mode.SRC_IN)


        toast.setGravity(Gravity.TOP, 0, 120)
        toast.show()
        Handler().postDelayed({ toast.cancel() }, durationInMillis)
    }
}
