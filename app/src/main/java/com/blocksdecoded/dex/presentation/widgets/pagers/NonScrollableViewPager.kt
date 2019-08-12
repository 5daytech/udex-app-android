package com.blocksdecoded.dex.presentation.widgets.pagers

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

open class NonScrollableViewPager: ViewPager {
    var scrollEnabled = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (scrollEnabled) {
            return super.onTouchEvent(ev)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (scrollEnabled) {
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }
}