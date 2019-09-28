package com.blocksdecoded.dex.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R

abstract class BaseDialog(
    private val layoutId: Int
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = activity?.let {
            val theme = if (App.appPreferences.isLightModeEnabled)
                R.style.LightFloatingDialog
            else
                R.style.DarkFloatingDialog

            Dialog(it, theme)
        }

        requireNotNull(dialog)

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutId, container, false)
}