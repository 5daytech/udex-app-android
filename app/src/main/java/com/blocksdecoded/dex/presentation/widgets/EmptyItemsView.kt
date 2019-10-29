package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.R
import kotlinx.android.synthetic.main.view_empty_items.view.*

class EmptyItemsView : ConstraintLayout {
    var title: String = ""
        set(value) {
            field = value
            empty_items_message?.text = value
        }

    init { View.inflate(context, R.layout.view_empty_items, this) }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init(attrs) }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.EmptyItemsView, 0, 0)
            try {
                val titleResId = ta.getResourceId(R.styleable.EmptyItemsView_eiv_message, 0)
                title = if (titleResId > 0) {
                    context.getString(titleResId)
                } else {
                    ta.getString(R.styleable.InfoItemView_iiv_title) ?: "Empty"
                }
            } finally {
                ta.recycle()
            }
        }
    }
}
