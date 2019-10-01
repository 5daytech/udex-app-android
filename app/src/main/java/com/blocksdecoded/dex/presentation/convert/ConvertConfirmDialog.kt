package com.blocksdecoded.dex.presentation.convert

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import java.math.BigDecimal

class ConvertConfirmDialog(
    var confirmInfo: ConvertConfirmInfo
): BaseDialog(R.layout.dialog_convert_confirm) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun show(fragmentManager: FragmentManager, confirmInfo: ConvertConfirmInfo) {
            val dialog = ConvertConfirmDialog(confirmInfo)
            dialog.show(fragmentManager, "convert_confirm")
        }
    }
}

data class ConvertConfirmInfo(
    val fromCoin: Coin,
    val fromAmount: BigDecimal,
    val toCoin: Coin,
    val toAmount: BigDecimal,
    val fee: BigDecimal,
    val processingDuration: Long
)