package com.blocksdecoded.dex.presentation.send

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.core.model.Coin
import java.math.BigDecimal

class SendConfirmDialog private constructor()
    : DialogFragment() {

    private var sendConfirmData: SendConfirmData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun show(fragmentManager: FragmentManager, data: SendConfirmData) {
            val dialog = SendConfirmDialog()



            dialog.show(fragmentManager, "send_confirm")
        }
    }

    data class SendConfirmData(
        val coin: Coin,
        val address: String,
        val amount: BigDecimal,
        val fee: BigDecimal,
        val onConfirm: () -> Unit
    )
}