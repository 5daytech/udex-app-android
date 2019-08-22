package com.blocksdecoded.dex.presentation.dialogs.processing

import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog

class ProcessingDialog:
	BaseDialog(R.layout.dialog_step_processing) {

	companion object {
		fun open(fragmentManager: FragmentManager) {
			val dialog = ProcessingDialog()

			dialog.show(fragmentManager, "processing")
		}
	}
}