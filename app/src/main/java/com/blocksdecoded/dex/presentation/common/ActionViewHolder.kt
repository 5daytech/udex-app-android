package com.blocksdecoded.dex.presentation.common

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.utils.setMargins
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_action.*

class ActionViewHolder(
    override val containerView: View,
    private val config: ActionConfig?,
    private val listener: Listener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        itemView.setOnClickListener { listener.onClick() }

        val margin = config?.topMargin ?: 0
        if (margin != 0) {
            item_action_container?.setMargins(0, margin, 0, margin)
        }

        item_action_icon?.setImageResource(config?.iconRes ?: 0)
        item_action_hint?.setText(config?.textRes ?: 0)

        item_action_icon?.setColorFilter(config?.tintColor ?: Color.BLACK, PorterDuff.Mode.SRC_IN)
        item_action_hint?.setTextColor(config?.tintColor ?: 0)
    }

    interface Listener {
        fun onClick()
    }

    data class ActionConfig(
        @DrawableRes val iconRes: Int,
        @StringRes val textRes: Int,
        @ColorInt val tintColor: Int,
        val topMargin: Int = 0
    )
}
