package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.ui.isVisible
import androidx.constraintlayout.widget.ConstraintLayout
import com.blocksdecoded.dex.utils.dp
import com.blocksdecoded.dex.utils.isValidIndex
import kotlinx.android.synthetic.main.view_drop_down.view.*

abstract class BaseDropDownView<T> : ConstraintLayout {
    init { inflate(R.layout.view_drop_down, attach = true) }

    var selectedView: View? = null
    var popupWindow: PopupWindow? = null
    var popupAdapter: PopupAdapter<T>? = null

    var itemResId: Int = 0
    var selectedItemPosition: Int? = null
        set(value) {
            field = value

            value?.let {
                popupAdapter?.getItem(value)?.let {

                    selectedView?.animate()
                        ?.alpha(0.8f)
                        ?.setDuration(100L)
                        ?.withEndAction { selectedView?.alpha = 1f }
                        ?.start()

                    refreshSelectedItem(it)
                }
            }
        }

    val isEmpty: Boolean
        get() = popupAdapter?.itemCount?.let { it == 0 } ?: true

    abstract fun refreshSelectedItem(item: T)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { loadAttrs(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { loadAttrs(attrs) }

    private fun loadAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BaseDropDownView, 0, 0)
            try {
                itemResId = ta.getResourceId(R.styleable.BaseDropDownView_ddv_item_layout, 0)
            } finally {
                ta.recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflateSelectedView()
    }

    private fun inflateSelectedView() {
        drop_down_selected_container?.removeAllViews()
        selectedView = drop_down_selected_container?.inflate(itemResId, attach = true)
        selectedView?.setBackgroundResource(R.color.transparent)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        popupWindow?.width = measuredWidth
    }

    private fun initPopup(adapter: PopupAdapter<T>, onItemPick: (position: Int) -> Unit) {
        popupAdapter = adapter.apply { this.onItemPick = onItemPick }
        popupAdapter?.dropDownView = this

        val view = inflate(R.layout.view_drop_down_popup)
        (view as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        popupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    @CallSuper
    open fun setData(data: List<T>) {
        popupAdapter?.setData(data)

        if (selectedItemPosition == null && data.isNotEmpty()) {
            selectedItemPosition = 0
        } else {
            selectedItemPosition = selectedItemPosition
        }
    }

    protected fun init(adapter: PopupAdapter<T>, onItemPick: (position: Int) -> Unit) {
        initPopup(adapter) {
            selectedItemPosition = it
            onItemPick(it)
            popupAdapter?.notifyDataSetChanged()
            popupWindow?.dismiss()
        }

        setOnClickListener {
            popupWindow?.showAsDropDown(this, 0, dp(-12f))
        }
    }

    abstract class PopupAdapter<T> (
        var items: List<T>,
        var onItemPick: ((position: Int) -> Unit)? = null
    ) : RecyclerView.Adapter<PopupAdapter.DropDownHolder<T>>() {
        lateinit var dropDownView: BaseDropDownView<T>

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: DropDownHolder<T>, position: Int) {
            holder.onBind(items[position], position != dropDownView.selectedItemPosition)
        }

        open fun setData(items: List<T>) {
            this.items = items
            notifyDataSetChanged()
        }

        open fun getItem(position: Int): T? = if (items.isValidIndex(position))
            items[position]
        else
            null

        abstract class DropDownHolder<T> (view: View, onItemPick: ((position: Int) -> Unit)? = null)
            : RecyclerView.ViewHolder(view) {
            init {
                itemView.setOnClickListener { onItemPick?.invoke(adapterPosition) }
            }

            abstract fun onBind(data: T)

            fun onBind(data: T, visible: Boolean) {
                isVisible = visible
                onBind(data)
            }
        }
    }
}