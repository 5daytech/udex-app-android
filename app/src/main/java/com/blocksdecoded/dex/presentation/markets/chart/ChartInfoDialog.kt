package com.blocksdecoded.dex.presentation.markets.chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.ui.CurrencyUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.ui.toPercentFormat
import kotlinx.android.synthetic.main.dialog_market_chart.*
import java.math.BigDecimal

class ChartInfoDialog : BaseBottomDialog(R.layout.dialog_market_chart) {

    lateinit var coinCode: String
    val viewModel: ChartInfoViewModel by lazy {
        ViewModelProviders.of(this).get(ChartInfoViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart_period_selector.addClickListener {
            viewModel.onPeriodSelect(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.init(coinCode)

        viewModel.coin.observe(this, Observer {
            chart_coin_code.text = "${it.title} "
        })

        viewModel.chartData.observe(this, Observer {
            val points = it.chartData.map { it.value }
            market_info_chart.displayData(points, R.color.chart, R.drawable.bg_chart)

            chart_coin_price.text = "$${it.rateValue?.toFiatDisplayFormat()}"

            val sign = if (it.diffValue >= BigDecimal.ZERO) "+" else "-"
            chart_change_percent.text = "$sign${it.diffValue.abs().toPercentFormat()}%"

            chart_market_cap.text = "$${CurrencyUtils.withSuffix(it.marketCap)}"
            chart_high.text = "$${it.highValue.toFiatDisplayFormat()}"
            chart_low.text = "$${it.lowValue.toFiatDisplayFormat()}"
        })

        viewModel.currentPeriod.observe(this, Observer {
            chart_period_selector.setSelectedView(it)
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