package com.fridaytech.dex.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.inflate
import com.fridaytech.dex.utils.isValidIndex
import com.fridaytech.dex.utils.ui.isVisible
import kotlinx.android.synthetic.main.view_drop_down.view.*

abstract class BaseDropDownView<T> : ConstraintLayout {
    init { inflate(R.layout.view_drop_down, attach = true) }

    abstract val popupVerticalOffset: Int

    private var arrowRotation = 0f
    private var rotationDuration = 200L

    var selectedView: View? = null
    private var popupWindow: PopupWindow? = null
    var popupAdapter: PopupAdapter<T>? = null

    var itemResId: Int = 0
    var selectedItemPosition: Int? = null
        set(value) {
            field = value

            value?.let {
                popupAdapter?.getItem(value)?.let {
                    refreshSelectedItem(it)
                }
            }
        }

    val isEmpty: Boolean
        get() = popupAdapter?.itemCount?.let { it == 0 } ?: true

    abstract fun refreshSelectedItem(item: T)

    //region Constructor

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { loadAttrs(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { loadAttrs(attrs) }

    //endregion

    //region Lifecycle

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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        popupWindow?.width = measuredWidth
    }

    //endregion

    //region Init

    protected fun init(adapter: PopupAdapter<T>, onItemPick: (position: Int) -> Unit) {
        initPopup(adapter) {
            selectedItemPosition = it
            onItemPick(it)
            popupAdapter?.notifyDataSetChanged()
            popupWindow?.dismiss()
        }

        setOnClickListener {
            popupWindow?.showAsDropDown(this, 0, popupVerticalOffset)
            toggleDropArrow()
        }
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

        popupWindow?.setOnDismissListener {
            toggleDropArrow()
        }
    }

    protected open fun inflateSelectedView() {
        drop_down_selected_container?.removeAllViews()
        selectedView = drop_down_selected_container?.inflate(itemResId, attach = true)
        selectedView?.setBackgroundResource(R.color.transparent)
    }

    //endregion

    private fun updateEnabled() {
        isClickable = popupAdapter?.itemCount ?: 0 > 1
        drop_down_arrow?.isInvisible = !isClickable
    }

    private fun toggleDropArrow() {
        drop_down_arrow?.clearAnimation()

        val rotationDiff = -180f
        arrowRotation = if (arrowRotation == 0f) rotationDiff else 0f

        drop_down_arrow?.animate()
            ?.rotation(arrowRotation)
            ?.setDuration(rotationDuration)
            ?.withEndAction { drop_down_arrow?.rotation = arrowRotation }
            ?.start()
    }

    @CallSuper
    open fun setData(data: List<T>) {
        popupAdapter?.setData(data)

        selectedItemPosition = if (selectedItemPosition == null && data.isNotEmpty()) {
            0
        } else {
            selectedItemPosition
        }

        updateEnabled()
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

        abstract class DropDownHolder<T> (
            view: View,
            onItemPick: ((position: Int) -> Unit)? = null
        ) : RecyclerView.ViewHolder(view) {
            abstract fun onBind(data: T)

            init {
                itemView.setOnClickListener {
                    onItemPick?.invoke(adapterPosition)
                }
            }

            fun onBind(data: T, visible: Boolean) {
                isVisible = visible
                onBind(data)
            }
        }
    }
}
