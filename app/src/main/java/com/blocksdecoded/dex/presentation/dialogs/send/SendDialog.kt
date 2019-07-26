package com.blocksdecoded.dex.presentation.dialogs.send

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.HudHelper
import com.blocksdecoded.dex.utils.QrUtils
import com.blocksdecoded.dex.utils.ShareUtils
import kotlinx.android.synthetic.main.dialog_receive.*
import kotlinx.android.synthetic.main.dialog_send.*
import kotlinx.android.synthetic.main.view_amount_input.*

class SendDialog: BaseBottomDialog(R.layout.dialog_send)  {
    var coinCode: String = ""

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = App.adapterManager
                .adapters
                .firstOrNull { it.coin.code == coinCode }

        if (adapter == null) {
            HudHelper.showErrorMessage(R.string.error_invalid_coin)
            return
        }

        send_amount.bindInitial( onMaxClick = {
            amount_input.setText(adapter.availableBalance(adapter.receiveAddress, FeeRatePriority.MEDIUM).toString())
        }, onSwitchClick = {

        })

        send_amount.updateAmountPrefix(adapter.coin.code)
    }

    companion object {
        fun open(fm: FragmentManager, coinCode: String) {
            val fragment = SendDialog()

            fragment.coinCode = coinCode

            fragment.show(fm, "Send")
        }
    }
}