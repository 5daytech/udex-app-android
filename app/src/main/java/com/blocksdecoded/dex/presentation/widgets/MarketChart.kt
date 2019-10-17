package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.manager.rates.model.ChartPoint
import com.blocksdecoded.dex.utils.getAttr
import com.blocksdecoded.dex.utils.visible
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MarketChart: LineChart {
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
	
	init {
		this.setTouchEnabled(true)
		this.isDragEnabled = true
		this.setScaleEnabled(false)
		this.setDrawGridBackground(false)
		setGridBackgroundColor(context.theme.getAttr(R.attr.MainBackground) ?: 0)
		this.setPinchZoom(false)
		this.description?.isEnabled = false
		this.setDrawBorders(false)
		this.axisLeft?.isEnabled = false
		this.axisRight?.isEnabled = false
		this.xAxis?.isEnabled = false
		this.setBorderWidth(0f)
		this.legend.isEnabled = false
		this.setViewPortOffsets(0f, 0f, 0f, 0f)
	}
	
	fun displayData(
		data: List<ChartPoint>,
		@ColorRes lineColor: Int,
		@DrawableRes backgroundDrawable: Int,
		drawAnimationDuration: Int = 300
	) {
		this.visible = data.isNotEmpty()
		
		if (data.isEmpty()) {
			this.clear()
			return
		}
		
		this.resetZoom()
		this.zoomOut()
		val entries = arrayListOf<Entry>()

		data.forEach {
			try {
				entries.add(Entry(it.timestamp.toFloat(), it.value))
			} catch (e: Exception) {

			}
		}
		
		val dataSet = LineDataSet(entries, "")
		dataSet.setDrawCircleHole(false)
		dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
		dataSet.setDrawCircles(false)
		dataSet.cubicIntensity = 0.1f
		dataSet.setDrawFilled(true)
		dataSet.lineWidth = 1f
		dataSet.setDrawValues(false)
		
		dataSet.color = ContextCompat.getColor(context, lineColor)
		dataSet.fillDrawable = ContextCompat.getDrawable(context, backgroundDrawable)

		this.data = LineData(dataSet)
		this.animateXY(drawAnimationDuration + (drawAnimationDuration / 2), drawAnimationDuration)
	}
}