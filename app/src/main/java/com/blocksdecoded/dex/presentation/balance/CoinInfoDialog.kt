package com.blocksdecoded.dex.presentation.balance

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import kotlinx.android.synthetic.main.dialog_alert.*

class CoinInfoDialog : BaseDialog(R.layout.dialog_alert) {

    private var coin: Coin? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
        coin?.let {
            alert_title?.text = "${it.title} (${it.code})"
            alert_title?.textAlignment = textAlignment
            it.shortInfoRes?.let {
                alert_description?.setText(it)
                alert_description?.textAlignment = textAlignment
            }
        } ?: dismiss()

        alert_confirm?.setOnClickListener { dismiss() }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, coin: Coin) {
            val dialog = CoinInfoDialog()
            dialog.coin = coin
            dialog.show(fragmentManager, "coin_info")
        }
    }
}