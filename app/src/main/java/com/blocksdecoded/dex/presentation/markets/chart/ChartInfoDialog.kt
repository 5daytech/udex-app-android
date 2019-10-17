package com.blocksdecoded.dex.presentation.markets.chart

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.ui.toPercentFormat
import kotlinx.android.synthetic.main.dialog_market_info.*
import java.math.BigDecimal

class ChartInfoDialog : BaseBottomDialog(R.layout.dialog_market_info) {

    lateinit var coinCode: String
    val viewModel: ChartInfoViewModel by lazy {
        ViewModelProviders.of(this).get(ChartInfoViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.init(coinCode)

        viewModel.coin.observe(this, Observer {
            chart_coin_code.text = "${it.title} "
        })

        viewModel.chartData.observe(this, Observer {
            val points = it.chartData.map { it.value }
            market_info_chart.displayData(points, R.color.violet, R.drawable.bg_chart)

            chart_coin_price.text = "$${it.rateValue?.toFiatDisplayFormat()}"

            val sign = if (it.diffValue >= BigDecimal.ZERO) "+" else "-"
            chart_change_percent.text = "$sign${it.diffValue.abs().toPercentFormat()}%"
        })
    }

    companion object {
        fun show(fragmentManager: FragmentManager, coinCode: String) {
            val dialog = ChartInfoDialog()

            dialog.coinCode = coinCode

            dialog.show(fragmentManager, "market_info")
        }
    }
}