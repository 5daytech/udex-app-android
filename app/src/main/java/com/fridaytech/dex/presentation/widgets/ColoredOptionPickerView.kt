package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.dp

class ColoredOptionPickerView : LinearLayout {

    private var circleSize = 28f
    private var activeAlpha = 1f
    private var inactiveAlpha = 0.8f
    var activeItemPosition = 0
        set(value) {
            field = value
            updateCirclesAlpha()
        }

    private var changeListener: ((View, Int) -> Unit)? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        val colors = listOf(Color.RED, Color.YELLOW, Color.GREEN)
        initCircles(colors)
    }

    fun init(colors: List<Int>) {
        initCircles(colors)
    }

    override fun performClick(): Boolean {
        var targetPosition = activeItemPosition + 1

        if (targetPosition > childCount - 1) {
            targetPosition = 0
        }

        activeItemPosition = targetPosition
        changeListener?.invoke(this, targetPosition)

        return super.performClick()
    }

    fun setChangeListener(listener: (View, Int) -> Unit) {
        changeListener = listener
    }

    fun removeChangeListener() {
        changeListener = null
    }

    private fun initCircles(colors: List<Int>) {
        removeAllViews()
        colors.forEach {
            val circleView = getCircle(it)
            circleView.setOnClickListener {
                val itemPosition = indexOfChild(it)
                if (itemPosition == activeItemPosition) {
                    performClick()
                } else {
                    changeListener?.invoke(this, itemPosition)
                }
            }
            addView(circleView)
        }
        invalidate()
        updateCirclesAlpha()
    }

    private fun getCircle(color: Int): View {
        return ImageView(context).apply {
            layoutParams = LayoutParams(dp(circleSize), dp(circleSize))
            setImageResource(R.drawable.ic_circle_white)
            val padding = dp(4f)
            setPadding(padding, padding, padding, padding)
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    private fun updateCirclesAlpha() {
        children.forEach {
            it.alpha = inactiveAlpha
        }
        getChildAt(activeItemPosition).alpha = activeAlpha
    }
}