package com.blocksdecoded.dex.presentation.widgets.dialogs

import android.os.Bundle
import android.view.View
import com.blocksdecoded.dex.R
import kotlinx.android.synthetic.main.dialog_alert.*

class AlertDialogFragment(
    private var title: Int,
    private var description: Int,
    private var buttonText: Int,
    private var listener: Listener? = null
): BaseDialog(R.layout.dialog_alert) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alert_title?.setText(title)
        alert_description?.setText(description)
        alert_confirm?.setText(buttonText)
        alert_confirm?.setOnClickListener {
            listener?.onConfirmClick()
            dismiss()
        }
    }

    interface Listener {
        fun onConfirmClick()
    }

    companion object {
        fun newInstance(
            title: Int,
            description: Int,
            buttonText: Int,
            listener: Listener? = null
        ): AlertDialogFragment =
            AlertDialogFragment(title, description, buttonText, listener)
    }
}