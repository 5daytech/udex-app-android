package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.dp
import com.blocksdecoded.dex.utils.inflate
import kotlinx.android.synthetic.main.view_pager_indicator.view.*

class PagerCircleIndicator : ConstraintLayout {
    init { inflate(R.layout.view_pager_indicator, attach = true) }

    private val disabledAlpha = 0.4f
    private val selectedAlpha = 1f
    private val animDuration = 300L

    private var lastPage = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun bindViewPager(viewPager: ViewPager) {
        updateSelectedText(viewPager)

        initDots(viewPager.adapter?.count ?: 0)

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                updateSelectedText(viewPager)
                updateSelectedDot(position)
            }
        })
    }

    private fun initDots(amount: Int) {
        pager_indicators.removeAllViews()

        for (i in (0 until amount)) {
            val dotView = View(context)
            val dotMargin = dp(3f)
            val dotSize = dp(7f)
            val layoutParams = MarginLayoutParams(dotSize, dotSize)
            layoutParams.setMargins(dotMargin, dotMargin, dotMargin, dotMargin)
            dotView.layoutParams = layoutParams
            dotView.setBackgroundResource(R.drawable.ic_circle_white)
            dotView.alpha = if (i == 0) selectedAlpha else disabledAlpha
            pager_indicators.addView(dotView)
        }
    }

    private fun updateSelectedText(viewPager: ViewPager) {
        pager_indicator_text.text = "${viewPager.currentItem + 1}/${viewPager.adapter?.count ?: 0}"
    }

    private fun updateSelectedDot(position: Int) {
        pager_indicators.getChildAt(lastPage).animate().alpha(disabledAlpha).setDuration(animDuration).start()
        pager_indicators.getChildAt(position).animate().alpha(selectedAlpha).setDuration(animDuration).start()
        lastPage = position
    }
}