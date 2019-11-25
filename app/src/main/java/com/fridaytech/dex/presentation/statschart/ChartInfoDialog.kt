package com.fridaytech.dex.presentation.statschart

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.dialogs.BaseBottomDialog
import com.fridaytech.dex.presentation.widgets.listeners.SimpleChartListener
import com.fridaytech.dex.utils.TimeUtils
import com.fridaytech.dex.utils.bindChangePercent
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import kotlinx.android.synthetic.main.dialog_market_chart.*
import kotlinx.android.synthetic.main.view_chart_stats.*

class ChartInfoDialog : BaseBottomDialog(R.layout.dialog_market_chart) {

    lateinit var coinCode: String
    val viewModel: ChartInfoViewModel by lazy {
        ViewModelProviders.of(this).get(ChartInfoViewModel::class.java)
    }

    private val chartListener = object : SimpleChartListener() {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            chart_picked?.text = "$${e?.y?.toBigDecimal()?.toFiatDisplayFormat()}\n" +
                    "${TimeUtils.timestampToDisplay(e?.x?.toLong() ?: 0)}"
        }

        override fun onChartGestureStart(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {
            isCancelable = false
        }

        override fun onChartGestureEnd(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {
            isCancelable = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart_stats.bind(viewModel::onPeriodSelect, chartListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.init(coinCode)

        viewModel.coin.observe(this, Observer {
            chart_coin_code.text = "${it.code} "
        })

        viewModel.chartData.observe(this, Observer {
            chart_coin_price.text = "$${it.rateValue?.toFiatDisplayFormat()}"
            chart_change_percent.bindChangePercent(it.diffValue)

            chart_stats.showChartStats(it)
        })

        viewModel.currentPeriod.observe(this, Observer {
            chart_stats.setPeriod(it)
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            chart_stats.setLoading(isLoading)
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
