package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.getColorRes
import com.fridaytech.dex.utils.visible
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.view_toolbar.view.*

class MainToolbar : AppBarLayout {
    init { inflate(context, R.layout.view_toolbar, this) }

    var title = ""
        set(value) {
            field = value
            toolbar_title?.text = value
        }

    //region Init

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs) }

    private fun init(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MainToolbar, 0, 0)
        try {
            title = ta.getString(R.styleable.MainToolbar_mt_title) ?: ""
        } finally {
            ta.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setBackgroundResource(android.R.color.transparent)
        targetElevation = 0f
        toolbar_title?.text = title
    }

    //endregion

    fun bind(
        leftActionButton: ActionInfo? = null,
        rightActionButton: ActionInfo? = null
    ) {
        toolbar_left_action?.visible = leftActionButton != null
        leftActionButton?.let {
            toolbar_left_action?.setImageResource(it.iconRes)
            if (it.iconTint != 0) {
                toolbar_left_action?.imageTintList = ColorStateList.valueOf(context.getColorRes(it.iconTint))
            }
            toolbar_left_action?.setOnClickListener { leftActionButton.onClick() }
        }

        toolbar_right_action?.visible = rightActionButton != null
        rightActionButton?.let {
            if (it.textRes != 0) {
                toolbar_right_action_text?.setText(it.textRes)
            }
            toolbar_right_action_image?.setImageResource(it.iconRes)
            if (it.iconTint != 0) {
                toolbar_right_action_image?.imageTintList = ColorStateList.valueOf(context.getColorRes(it.iconTint))
            }
            toolbar_right_action?.setOnClickListener { rightActionButton.onClick() }
        }
    }

    fun setTitle(@StringRes stringRes: Int) {
        title = context.getString(stringRes)
    }

    data class ActionInfo(
        @DrawableRes val iconRes: Int,
        @ColorRes val iconTint: Int = 0,
        @StringRes val textRes: Int = 0,
        val onClick: () -> Unit
    )

    companion object {
        fun getBackAction(onClick: () -> Unit): ActionInfo =
            ActionInfo(
                R.drawable.ic_back,
                0,
                0,
                onClick
            )
    }
}
