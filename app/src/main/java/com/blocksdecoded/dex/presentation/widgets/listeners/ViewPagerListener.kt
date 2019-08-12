package com.blocksdecoded.dex.presentation.widgets.listeners

import androidx.viewpager.widget.ViewPager

abstract class ViewPagerListener: ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =
            Unit

    override fun onPageSelected(position: Int) = Unit
}