package com.fridaytech.dex.presentation.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.dialogs.BaseDialog
import com.fridaytech.dex.presentation.orders.model.CancelOrderInfo
import kotlinx.android.synthetic.main.dialog_cancel_confirm.*

class CancelOrderConfirmDialog : BaseDialog(R.layout.dialog_cancel_confirm) {

    lateinit var cancelOrderInfo: CancelOrderInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancel_confirm_btn?.setOnClickListener {
            cancelOrderInfo.onConfirm()
            dismiss()
        }

        cancel_fee?.setCoin(cancelOrderInfo.feeCoinCode ?: "", cancelOrderInfo.estimatedFee, isExactAmount = false)
        cancel_duration?.setMillis(cancelOrderInfo.processingDuration)

        cancel_title.setText(if (cancelOrderInfo.orderCount == 1) {
            R.string.orders_cancel
        } else {
            R.string.orders_cancel_all
        })
    }

    companion object {
        fun show(fragmentManager: FragmentManager, cancelInfo: CancelOrderInfo) {
            val dialog = CancelOrderConfirmDialog()

            dialog.cancelOrderInfo = cancelInfo

            dialog.show(fragmentManager, "cancel_order_confirm")
        }
    }
}
