package com.blocksdecoded.dex.presentation.dialogs.receive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.HudHelper
import com.blocksdecoded.dex.utils.QrUtils
import com.blocksdecoded.dex.utils.ShareUtils
import kotlinx.android.synthetic.main.dialog_receive.*

class ReceiveDialog: BaseBottomDialog(R.layout.dialog_receive)  {
    var coinCode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = App.adapterManager
                .adapters
                .firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            HudHelper.showErrorMessage(R.string.error_invalid_coin)
            return
        }

        receive_raw.text = adapter.receiveAddress
        receive_title.text = "Receive ${adapter.coin.title}"

        try {
            receive_qr.setImageBitmap(QrUtils.getBarcode(adapter.receiveAddress))
        } catch (e: Exception) {

        }

        receive_forward.setOnClickListener {
            ShareUtils.shareMessage(activity, adapter.receiveAddress)
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