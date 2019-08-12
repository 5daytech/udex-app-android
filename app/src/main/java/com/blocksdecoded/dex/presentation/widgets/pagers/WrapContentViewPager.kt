package com.blocksdecoded.dex.presentation.widgets.pagers

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import kotlin.math.roundToInt

class WrapContentViewPager : NonScrollableViewPager {
	private val TAG = WrapContentViewPager::class.java.simpleName
	private var wrapHeight: Int = 0
	private var decorHeight = 0
	private var widthMeasuredSpec: Int = 0
	
	private var animateHeight: Boolean = false
	private var rightHeight: Int = 0
	private var leftHeight: Int = 0
	private var scrollingPosition = -1
	
	constructor(context: Context) : super(context) { init() }
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
	
	private fun init() {
		addOnPageChangeListener(object : OnPageChangeListener {
			var state: Int = 0
			
			override fun onPageScrolled(position: Int, offset: Float, positionOffsetPixels: Int) {}
			
			override fun onPageSelected(position: Int) {
				if (state == SCROLL_STATE_IDLE) {
					wrapHeight = 0 // measure the selected page in-case it's a change without scrolling
					postInvalidate()
				}
			}
			
			override fun onPageScrollStateChanged(state: Int) {
				this.state = state
			}
		})
	}
	
	override fun setAdapter(adapter: PagerAdapter?) {
		if (adapter !is ObjectAtPosition) {
			throw IllegalArgumentException("WrapContentViewPage requires that PagerAdapter will implement ObjectAtPositionInterface")
		}
		wrapHeight = 0 // so we measure the new content in onMeasure
		super.setAdapter(adapter)
	}
	
	
	/**
	 * Allows to redraw the view size to wrap the content of the bigger child.
	 *
	 * @param widthMeasureSpec  with measured
	 * @param heightMeasureSpec wrapHeight measured
	 */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		var heightMeasureSpec = heightMeasureSpec
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		
		widthMeasuredSpec = widthMeasureSpec
		val mode = MeasureSpec.getMode(heightMeasureSpec)
		
		if (mode == MeasureSpec.UNSPECIFIED || mode == View.MeasureSpec.AT_MOST) {
			if (wrapHeight == 0) {
				// measure vertical decor (i.e. PagerTitleStrip) based on ViewPager implementation
				decorHeight = 0
				for (i in 0 until childCount) {
					val child = getChildAt(i)
					val lp = child.layoutParams as LayoutParams
					if (lp.isDecor) {
						val vgrav = lp.gravity and Gravity.VERTICAL_GRAVITY_MASK
						val consumeVertical = vgrav == Gravity.TOP || vgrav == Gravity.BOTTOM
						if (consumeVertical) {
							decorHeight += child.measuredHeight
						}
					}
				}
				
				// make sure that we have an wrapHeight (not sure if this is necessary because it seems that onPageScrolled is called right after
				val position = currentItem
				val child = getViewAtPosition(position)
				if (child != null) {
					wrapHeight = measureViewHeight(child)
				}
				Log.d(TAG, "onMeasure wrapHeight:$wrapHeight decor:$decorHeight")
				
			}
			val totalHeight = wrapHeight + decorHeight + paddingBottom + paddingTop
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY)
			Log.d(TAG, "onMeasure total wrapHeight:$totalHeight")
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
	
	public override fun onPageScrolled(position: Int, offset: Float, positionOffsetPixels: Int) {
		super.onPageScrolled(position, offset, positionOffsetPixels)
		// cache scrolled view heights
		if (scrollingPosition != position) {
			scrollingPosition = position
			// scrolled position is always the left scrolled page
			val leftView = getViewAtPosition(position)
			val rightView = getViewAtPosition(position + 1)
			if (leftView != null && rightView != null) {
				leftHeight = measureViewHeight(leftView)
				rightHeight = measureViewHeight(rightView)
				animateHeight = true
				Log.d(TAG, "onPageScrolled heights left:$leftHeight right:$rightHeight")
			} else {
				animateHeight = false
			}
		}
		
		if (animateHeight) {
			val newHeight = (leftHeight * (1 - offset) + rightHeight * offset).roundToInt()
			if (wrapHeight != newHeight) {
				Log.d(TAG, "onPageScrolled wrapHeight change:$newHeight")
				wrapHeight = newHeight
				requestLayout()
				invalidate()
			}
		}
	}
	
	private fun measureViewHeight(view: View): Int {
		view.measure(
			ViewGroup.getChildMeasureSpec(widthMeasuredSpec, paddingLeft + paddingRight, view.layoutParams.width),
			MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
		)
		return view.measuredHeight
	}
	
	private fun getViewAtPosition(position: Int): View? {
		if (adapter != null) {
			val objectAtPosition = (adapter as ObjectAtPosition).getObject(position)
			if (objectAtPosition != null) {
				for (i in 0 until childCount) {
					val child = getChildAt(i)
					if (child != null && adapter!!.isViewFromObject(child, objectAtPosition)) {
						return child
					}
				}
			}
		}
		return null
	}
	
	
	interface ObjectAtPosition {
		
		fun getObject(position: Int): Any?
		
	}
}