package com.fridaytech.dex.presentation.receive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fridaytech.dex.App
import com.fridaytech.dex.R
import com.fridaytech.dex.data.manager.clipboard.ClipboardManager
import com.fridaytech.dex.presentation.dialogs.BaseBottomDialog
import com.fridaytech.dex.utils.ui.QrUtils
import com.fridaytech.dex.utils.ui.ShareUtils
import com.fridaytech.dex.utils.ui.ToastHelper
import kotlinx.android.synthetic.main.dialog_receive.*

class ReceiveDialog private constructor() :
    BaseBottomDialog(R.layout.dialog_receive) {
    var coinCode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = App.adapterManager.adapters
            .firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            ToastHelper.showErrorMessage(R.string.error_invalid_coin)
            dismiss()
            return
        }

        val receiveAddress = adapter.receiveAddress

        receive_address?.update(receiveAddress)
        receive_coin_name?.text = adapter.coin.title
        receive_coin_icon?.bind(coinCode)

        try {
            receive_qr?.setImageBitmap(QrUtils.getBarcode(receiveAddress))
        } catch (e: Exception) {
        }

        receive_forward?.setOnClickListener {
            ShareUtils.shareMessage(activity, receiveAddress)
        }

        receive_address?.bind {
            ClipboardManager.copyText(receiveAddress)
            ToastHelper.showSuccessMessage(R.string.message_copied, 1000)
        }
    }

    companion object {
        fun open(fm: FragmentManager, coinCode: String) {
            val fragment = ReceiveDialog()

            fragment.coinCode = coinCode

            fragment.show(fm, "Receive")
        }
    }
}
