package com.blocksdecoded.dex.presentation.markets.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import kotlinx.android.synthetic.main.dialog_market_info.*

class MarketInfoDialog : BaseBottomDialog(R.layout.dialog_market_info) {

    lateinit var coinCode: String
    val viewModel: MarketInfoViewModel by lazy {
        ViewModelProviders.of(this).get(MarketInfoViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.init(coinCode)

        viewModel.coin.observe(this, Observer {
            market_coin_code.text = "${it.title} "
        })
    }

    companion object {
        fun show(fragmentManager: FragmentManager, coinCode: String) {
            val dialog = MarketInfoDialog()

            dialog.coinCode = coinCode

            dialog.show(fragmentManager, "market_info")
        }
    }
}