package com.blocksdecoded.dex.presentation.orders

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.orders.model.ExchangePairViewItem
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.ui.isVisible
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.view_drop_down.view.*

class DropDownView: ConstraintLayout {
    init { inflate(R.layout.view_drop_down, attach = true) }

    private var popupWindow: PopupWindow? = null
    private var popupAdapter: PopupAdapter? = null

    var itemResId: Int = 0
    var selectedItemPosition: Int? = null
        set(value) {
            field = value
            refreshSelectedItem()
        }

    var selectedView: View? = null
    val isEmpty: Boolean
        get() = popupAdapter?.itemCount?.let { it == 0 } ?: true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { loadAttrs(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { loadAttrs(attrs) }

    private fun loadAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.DropDownView, 0, 0)
            try {
                itemResId = ta.getResourceId(R.styleable.DropDownView_ddv_item_layout, 0)
            } finally {
                ta.recycle()
            }
        }
    }

    private fun inflateSelectedView() {
        drop_down_selected_container?.removeAllViews()
        selectedView = drop_down_selected_container?.inflate(itemResId, attach = true)
    }

    private fun refreshSelectedItem() {
        val baseCoin: TextView? = selectedView?.findViewById(R.id.exchange_pair_base_coin)
        val quoteCoin: TextView? = selectedView?.findViewById(R.id.exchange_pair_quote_coin)
        val basePrice: TextView? = selectedView?.findViewById(R.id.exchange_pair_base_coin_price)
        val quotePrice: TextView? = selectedView?.findViewById(R.id.exchange_pair_quote_coin_price)

        selectedItemPosition?.let {
            popupAdapter?.getItem(it)?.let { data ->
                baseCoin?.text = data.baseCoin
                quoteCoin?.text = data.quoteCoin
                basePrice?.text = "$${data.basePrice?.toFiatDisplayFormat()}"
                quotePrice?.text = "$${data.quotePrice?.toFiatDisplayFormat()}"
            }
        }
    }

    private fun initPopup(onItemPick: (position: Int) -> Unit) {
        popupAdapter = PopupAdapter(
            listOf(),
            onItemPick
        )
        popupAdapter?.dropDownView = this

        val view = inflate(R.layout.view_drop_down_popup)
        (view as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popupAdapter
        }

        popupWindow = PopupWindow(
            view,
            WindowManager.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflateSelectedView()
    }

    fun init(onItemPick: (position: Int) -> Unit) {
        initPopup {
            selectedItemPosition = it
            onItemPick(it)
            popupAdapter?.notifyDataSetChanged()
            popupWindow?.dismiss()
        }

        setOnClickListener {
            popupWindow?.showAsDropDown(this)
        }
    }

    fun notifyDataSetChanged() {

    }

    fun setData(pairs: List<ExchangePairViewItem>) {
        popupAdapter?.setData(pairs)

        if (selectedItemPosition == null) {
            selectedItemPosition = 0
        }
    }

    private class PopupAdapter (
        var items: List<ExchangePairViewItem>,
        val onItemPick: (position: Int) -> Unit
    ): RecyclerView.Adapter<PopupAdapter.Holder>() {
        lateinit var dropDownView: DropDownView

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(parent.inflate(R.layout.item_exchange_pair), onItemPick)

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.onBind(items[position], position != dropDownView.selectedItemPosition)
        }

        fun setData(items: List<ExchangePairViewItem>) {
            this.items = items
            notifyDataSetChanged()
        }

        fun getItem(position: Int): ExchangePairViewItem = items[position]

        private class Holder (
            view: View,
            onItemPick: (position: Int) -> Unit
        ) : RecyclerView.ViewHolder(view) {
            val baseCoin: TextView = itemView.findViewById(R.id.exchange_pair_base_coin)
            val quoteCoin: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin)
            val basePrice: TextView = itemView.findViewById(R.id.exchange_pair_base_coin_price)
            val quotePrice: TextView = itemView.findViewById(R.id.exchange_pair_quote_coin_price)

            init {
                itemView.setOnClickListener { onItemPick(adapterPosition) }
            }

            fun onBind(data: ExchangePairViewItem, visible: Boolean) {
                isVisible = visible

                baseCoin.text = data.baseCoin
                quoteCoin.text = data.quoteCoin
                basePrice.text = "$${data.basePrice?.toFiatDisplayFormat()}"
                quotePrice.text = "$${data.quotePrice?.toFiatDisplayFormat()}"
            }
        }
    }
}