package com.fridaytech.dex.presentation.settings

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fridaytech.dex.R
import kotlinx.android.synthetic.main.view_theme_switch.view.*

class ThemeSwitchView : ConstraintLayout {
    init { View.inflate(context, R.layout.view_theme_switch, this) }

    var selectedTheme: Int = 0
        get() = settings_item_switch?.activeItemPosition ?: 0
        set(value) {
            switchOnCheckedChangeListener = null
            settings_item_switch?.activeItemPosition = value
            field = value
            invalidate()
        }

    var switchOnCheckedChangeListener: ThemeSwitchListener? = null
        set(value) {
            if (value == null) {
                settings_item_switch.removeChangeListener()
            } else {
                settings_item_switch.setChangeListener { _, state ->
                    switchOnCheckedChangeListener?.onChange(state)
                }
            }
            field = value
            invalidate()
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init(colors: List<Int>) {
        settings_item_switch.init(colors)
    }

    fun toggleSwitch() {
        settings_item_switch?.performClick()
    }

    interface ThemeSwitchListener {
        fun onChange(state: Int)
    }
}
