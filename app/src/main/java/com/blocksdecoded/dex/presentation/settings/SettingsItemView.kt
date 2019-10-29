package com.blocksdecoded.dex.presentation.settings

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.visible
import kotlin.math.absoluteValue
import kotlinx.android.synthetic.main.view_settings_item.view.*

class SettingsItemView : ConstraintLayout {
    init { View.inflate(context, R.layout.view_settings_item, this) }

    private var title: String = "name"
        set(value) {
            field = value
            settings_item_title?.text = value
        }

    private var iconRes: Int = R.drawable.ic_about
        set(value) {
            field = value
            settings_item_icon?.setImageResource(value)
        }

    private var tint: Int = 0
        set(value) {
            field = value
            if (value.absoluteValue > 0) {
                settings_item_icon?.setColorFilter(value, PorterDuff.Mode.SRC_IN)
                settings_item_chevron?.setColorFilter(value, PorterDuff.Mode.SRC_IN)
                settings_item_title?.setTextColor(value)
            }
        }

    private var actionSwitch: Boolean = false
        set(value) {
            field = value
            settings_item_chevron?.visible = !actionSwitch
            settings_item_switch?.visible = actionSwitch
        }

    var isChecked: Boolean = false
        get() = settings_item_switch?.isChecked ?: false
        set(value) {
            switchOnCheckedChangeListener = null
            settings_item_switch?.isChecked = value
            field = value
            invalidate()
        }

    var switchOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
        set(value) {
            settings_item_switch.setOnCheckedChangeListener(value)
            invalidate()
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsItemView, 0, 0)
            try {
                val nameResId = ta.getResourceId(R.styleable.SettingsItemView_siv_title, 0)

                title = if (nameResId > 0) {
                    context.getString(nameResId)
                } else {
                    ta.getString(R.styleable.SettingsItemView_siv_title) ?: "Attr"
                }

                iconRes = ta.getResourceId(R.styleable.SettingsItemView_siv_icon, R.drawable.ic_about)
                actionSwitch = ta.getBoolean(R.styleable.SettingsItemView_siv_action_switch, false)
                tint = ta.getColor(R.styleable.SettingsItemView_siv_tint, 0)
            } finally {
                ta.recycle()
            }
        }
    }

    fun toggleSwitch() {
        settings_item_switch?.toggle()
    }

    fun setInfoBadgeVisible(isVisible: Boolean) {
        settings_item_badge?.visible = isVisible
    }
}
