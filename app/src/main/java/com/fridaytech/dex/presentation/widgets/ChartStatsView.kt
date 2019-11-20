package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.fridaytech.dex.R
import com.fridaytech.dex.presentation.statschart.ChartViewItem
import com.fridaytech.dex.utils.inflate
import com.fridaytech.dex.utils.listeners.SimpleChartListener
import com.fridaytech.dex.utils.ui.NumberUtils
import com.fridaytech.dex.utils.ui.toFiatDisplayFormat
import com.fridaytech.dex.utils.visible
import kotlinx.android.synthetic.main.view_chart_stats.view.*

class ChartStatsView : LinearLayout {

    init {
        inflate(R.layout.view_chart_stats, attach = true)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun bind(
        onPeriodSelected: (Int) -> Unit,
        chartListener: SimpleChartListener
    ) {
        chart_period_selector.addClickListener(onPeriodSelected)

        market_info_chart.setOnChartValueSelectedListener(chartListener)
        market_info_chart.onChartGestureListener = chartListener
    }

    fun showChartStats(it: ChartViewItem) {
        market_info_chart.displayData(it.chartData, R.color.chart, R.drawable.bg_chart)

        chart_market_cap.text = "$${NumberUtils.withSuffix(it.marketCap)}"
        chart_high.text = "$${it.highValue.toFiatDisplayFormat()}"
        chart_low.text = "$${it.lowValue.toFiatDisplayFormat()}"
    }

    fun setPeriod(position: Int) {
        chart_period_selector.setSelectedView(position)
    }

    fun setLoading(isLoading: Boolean) {
        market_info_chart.visible = !isLoading
        chart_progress.visible = isLoading
    }
}
