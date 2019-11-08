package com.blocksdecoded.dex.presentation.statschart

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseBottomDialog
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.bindChangePercent
import com.blocksdecoded.dex.utils.listeners.SimpleChartListener
import com.blocksdecoded.dex.utils.ui.NumberUtils
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import kotlinx.android.synthetic.main.dialog_market_chart.*

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
        chart_period_selector.addClickListener {
            viewModel.onPeriodSelect(it)
        }

        market_info_chart.setOnChartValueSelectedListener(chartListener)
        market_info_chart.onChartGestureListener = chartListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.init(coinCode)

        viewModel.coin.observe(this, Observer {
            chart_coin_code.text = "${it.title} "
        })

        viewModel.chartData.observe(this, Observer {
            market_info_chart.displayData(it.chartData, R.color.chart, R.drawable.bg_chart)

            chart_coin_price.text = "$${it.rateValue?.toFiatDisplayFormat()}"

            chart_change_percent.bindChangePercent(it.diffValue)
            chart_market_cap.text = "$${NumberUtils.withSuffix(it.marketCap)}"
            chart_high.text = "$${it.highValue.toFiatDisplayFormat()}"
            chart_low.text = "$${it.lowValue.toFiatDisplayFormat()}"
        })

        viewModel.currentPeriod.observe(this, Observer {
            chart_period_selector.setSelectedView(it)
        })

        viewModel.loading.observe(this, Observer { loading ->
            market_info_chart.visible = !loading
            chart_progress.visible = loading
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
