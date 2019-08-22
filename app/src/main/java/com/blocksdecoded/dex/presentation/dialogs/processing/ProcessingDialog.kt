package com.blocksdecoded.dex.presentation.dialogs.processing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import kotlinx.android.synthetic.main.dialog_step_processing.*

class ProcessingDialog:
	BaseDialog(R.layout.dialog_step_processing) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		processing_timer?.bind(60) {
			dismiss()
		}
	}

	companion object {
		fun open(fragmentManager: FragmentManager): DialogFragment {
			val dialog = ProcessingDialog()

			dialog.isCancelable = false

			dialog.show(fragmentManager, "processing")

			return dialog
		}
	}
}