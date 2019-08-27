package com.blocksdecoded.dex.presentation.send

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

class SendConfirmDialog private constructor()
    : DialogFragment() {

    companion object {
        fun show(fragmentManager: FragmentManager, data: SendConfirmData) {

        }
    }

    data class SendConfirmData(
        val coin: Coin,
        val address: String,
        val amount: BigDecimal,
        val fee: BigDecimal
    )

    interface SendConfirmListener {
        fun onConfirm()

        fun onDismiss()
    }
}