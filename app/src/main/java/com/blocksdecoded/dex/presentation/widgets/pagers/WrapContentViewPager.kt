package com.blocksdecoded.dex.presentation.widgets.pagers

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class WrapContentViewPager : NonScrollableViewPager {
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	
	private fun init() {
		addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
			override fun onPageSelected(position: Int) {
				requestLayout()
			}
		})
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		var heightMeasureSpec = heightMeasureSpec
		val mode = MeasureSpec.getMode(heightMeasureSpec)
		
		if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			val child = getChildAt(currentItem)
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
			val height = child.measuredHeight
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}