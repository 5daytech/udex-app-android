package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.blocksdecoded.dex.utils.Logger
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
		this.setTouchEnabled(false)
		this.isDragEnabled = false
		this.setScaleEnabled(true)
		this.setDrawGridBackground(false)
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
		data: List<Float>,
		@ColorRes color: Int,
		@DrawableRes backgroundDrawable: Int
	) {
		this.visible = data.isNotEmpty()
		
		if (data.isEmpty()) {
			this.clear()
			return
		}
		
		this.resetZoom()
		this.zoomOut()
		val entries = arrayListOf<Entry>()
		
		data.forEachIndexed { index, fl ->
			try {
				entries.add(Entry(index.toFloat(), fl))
			} catch (e: Exception) {
				Logger.e(e)
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
		
		dataSet.color = ContextCompat.getColor(context, color)
		dataSet.fillDrawable = ContextCompat.getDrawable(context, backgroundDrawable)
		
		this.data = LineData(dataSet)
		this.animateX(300)
	}
}