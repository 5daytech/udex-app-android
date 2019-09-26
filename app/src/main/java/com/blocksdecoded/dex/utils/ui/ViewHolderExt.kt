package com.blocksdecoded.dex.utils.ui

import android.animation.ValueAnimator
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.annotation.AttrRes
import com.blocksdecoded.dex.utils.getAttr

var RecyclerView.ViewHolder.height
    get() = itemView.layoutParams.height
    set(value) {
        itemView.layoutParams.height = value
    }

var RecyclerView.ViewHolder.isVisible: Boolean
    get() = itemView.visibility == View.VISIBLE
    set(value) {
        itemView.visibility = if (value) View.VISIBLE else View.GONE

        itemView.layoutParams = if (value)
            RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        else
            RecyclerView.LayoutParams(0, 0)
    }

fun RecyclerView.ViewHolder.getAttr(@AttrRes resId: Int): Int =
    itemView.context.theme.getAttr(resId) ?: 0

fun RecyclerView.ViewHolder.updateHeight(height: Int, animated: Boolean = false) {
    if (animated) {
        val start = this.itemView.layoutParams.height

        if (start == height) {
            this.height = height
            return
        }

        val valueAnimator = ValueAnimator.ofInt(start, height)

        valueAnimator.addUpdateListener {
            this.height = it.animatedValue as Int
            this.itemView.invalidate()
        }

        valueAnimator.duration = 200
        valueAnimator.start()
    } else {
        this.height = height
    }
}