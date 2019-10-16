package com.blocksdecoded.dex.presentation.markets.info

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog

class MarketInfoDialog : BaseBottomDialog(R.layout.dialog_market_info) {

    lateinit var coinCode: String
    val viewModel: MarketInfoViewModel by lazy {
        ViewModelProviders.of(this).get(MarketInfoViewModel::class.java)
    }

    companion object {
        fun show(fragmentManager: FragmentManager, coinCode: String) {
            val dialog = MarketInfoDialog()

            dialog.coinCode = coinCode

            dialog.show(fragmentManager, "market_info")
        }
    }
}