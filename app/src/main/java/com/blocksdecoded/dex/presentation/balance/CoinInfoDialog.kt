package com.blocksdecoded.dex.presentation.balance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.presentation.widgets.dialogs.BaseDialog
import kotlinx.android.synthetic.main.dialog_coin_info.*

class CoinInfoDialog : BaseDialog(R.layout.dialog_coin_info) {

    private var coin: Coin? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coin?.let {
            info_title?.text = "${it.title} (${it.code})"
            it.shortInfoRes?.let { info_description?.setText(it) }
        } ?: dismiss()

        info_confirm?.setOnClickListener { dismiss() }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, coin: Coin) {
            val dialog = CoinInfoDialog()
            dialog.coin = coin
            dialog.show(fragmentManager, "coin_info")
        }
    }
}