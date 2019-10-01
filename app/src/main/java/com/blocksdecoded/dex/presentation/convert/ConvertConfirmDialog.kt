package com.blocksdecoded.dex.presentation.convert

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.convert.model.ConvertType
import com.blocksdecoded.dex.presentation.convert.model.ConvertType.*
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import kotlinx.android.synthetic.main.dialog_convert_confirm.*
import java.math.BigDecimal

class ConvertConfirmDialog : BaseDialog(R.layout.dialog_convert_confirm) {
    lateinit var confirmInfo: ConvertConfirmInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        convert_confirm_from_coin?.bind(confirmInfo.fromCoin.code)
        convert_confirm_to_coin?.bind(confirmInfo.toCoin.code)
        convert_confirm_from_amount?.text = "${confirmInfo.fromAmount.toDisplayFormat()} ${confirmInfo.fromCoin.code}"
        convert_confirm_to_amount?.text = "${confirmInfo.toAmount.toDisplayFormat()} ${confirmInfo.toCoin.code}"
        convert_fee?.setCoin(confirmInfo.feeCoinCode ?: "", confirmInfo.fee, false)
        convert_processing_time?.setMillis(confirmInfo.processingDuration)

        convert_confirm_btn?.setOnClickListener {
            confirmInfo.onConfirm()
            dismiss()
        }

        dialog_title?.text = when(confirmInfo.action) {
            WRAP -> "Wrap"
            UNWRAP -> "Unwrap"
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, confirmInfo: ConvertConfirmInfo) {
            val dialog = ConvertConfirmDialog()
            dialog.confirmInfo = confirmInfo
            dialog.show(fragmentManager, "convert_confirm")
        }
    }
}

data class ConvertConfirmInfo(
    val action: ConvertType,
    val fromCoin: Coin,
    val fromAmount: BigDecimal,
    val toCoin: Coin,
    val toAmount: BigDecimal,
    val fee: BigDecimal?,
    val feeCoinCode: String?,
    val processingDuration: Long,
    val onConfirm: () -> Unit
)