package com.fridaytech.dex.presentation.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.fridaytech.dex.utils.Logger
import com.fridaytech.dex.utils.ThemeHelper

abstract class BaseDialog(
    private val layoutId: Int
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = activity?.let {
            Dialog(it, ThemeHelper.getFloatingDialogTheme())
        }

        requireNotNull(dialog)

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = try {
        inflater.inflate(layoutId, container, false)
    } catch (e: Exception) {
        Logger.e(e)
        null
    }
}
