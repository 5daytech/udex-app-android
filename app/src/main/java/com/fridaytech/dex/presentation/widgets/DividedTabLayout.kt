package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.fridaytech.dex.R
import com.google.android.material.tabs.TabLayout

class DividedTabLayout : TabLayout {
    constructor(context: Context?) : super(context) {
        init(null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val root = this.getChildAt(0)
        if (root is LinearLayout) {
            root.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            root.dividerPadding = 30
            root.dividerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_vertical_dots)
        }
    }
}
