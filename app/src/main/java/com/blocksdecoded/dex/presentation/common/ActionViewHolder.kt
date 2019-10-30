package com.blocksdecoded.dex.presentation.common

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_action.*

class ActionViewHolder(
    override val containerView: View,
    private val config: ActionConfig?,
    private val listener: Listener
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        itemView.setOnClickListener { listener.onClick() }

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
        @ColorInt val tintColor: Int
    )
}
