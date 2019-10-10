package com.blocksdecoded.dex.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.inflate
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_numpad_button.*
import android.view.MotionEvent
import android.view.GestureDetector

class NumPadView: RecyclerView {
    var showFingerPrintButton: Boolean = false
        set(value) {
            field = value
            numPadAdapter?.showFingerPrintButton = value
        }

    private var numPadAdapter: NumPadItemsAdapter? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    @SuppressLint("ClickableViewAccessibility")
    fun bind(
        listener: NumPadItemsAdapter.Listener,
        bottomLeftButtonType: NumPadItemType,
        showLetters: Boolean = true,
        scrollable: Boolean = false
    ) {
        removeAllViewsInLayout()

        if (numPadAdapter != null) throw Exception("ExchangePairsAdapter already initialized")

        layoutManager = GridLayoutManager(context, 3)
        numPadAdapter = NumPadItemsAdapter(listener, bottomLeftButtonType, showLetters)
        adapter = numPadAdapter

        if (!scrollable) {
            //disables BottomSheet dragging in numpad area
            this.setOnTouchListener { _, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_UP -> false
                    else -> true
                }
            }
        }
    }
}

class NumPadItemsAdapter(
    private val listener: Listener,
    bottomLeftButtonType: NumPadItemType,
    private val showLetters: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val numPadItems = listOf(
            NumPadItem(NumPadItemType.NUMBER, 1, ""),
            NumPadItem(NumPadItemType.NUMBER, 2, "abc"),
            NumPadItem(NumPadItemType.NUMBER, 3, "def"),
            NumPadItem(NumPadItemType.NUMBER, 4, "ghi"),
            NumPadItem(NumPadItemType.NUMBER, 5, "jkl"),
            NumPadItem(NumPadItemType.NUMBER, 6, "mno"),
            NumPadItem(NumPadItemType.NUMBER, 7, "pqrs"),
            NumPadItem(NumPadItemType.NUMBER, 8, "tuv"),
            NumPadItem(NumPadItemType.NUMBER, 9, "wxyz"),
            NumPadItem(bottomLeftButtonType, 0, "Bottom Left"),
            NumPadItem(NumPadItemType.NUMBER, 0, ""),
            NumPadItem(NumPadItemType.DELETE, 0, "Bottom Right")
    )

    var showFingerPrintButton = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NumPadItemViewHolder(parent.inflate(R.layout.item_numpad_button),
            onClick = { position ->
                listener.onItemClick(numPadItems[position])
            },
            onLongClick = { position ->
                listener.onItemLongClick(numPadItems[position])
            })
    }

    override fun getItemCount() = numPadItems.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NumPadItemViewHolder) {
            holder.bind(numPadItems[position], showFingerPrintButton, showLetters)
        }
    }

    interface Listener {
        fun onItemClick(item: NumPadItem)

        fun onItemLongClick(item: NumPadItem)
    }
}

data class NumPadItem(val type: NumPadItemType, val number: Int, val letters: String)

enum class NumPadItemType {
    NUMBER, DELETE, FINGER, DOT
}

class NumPadItemViewHolder(
    override val containerView: View,
    private val onClick: (position: Int) -> Unit,
    private val onLongClick: (position: Int) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    private val gestureDetector = GestureDetector(containerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            onLongClick.invoke(adapterPosition)
        }
    })

    fun bind(item: NumPadItem, isFingerprintEnabled: Boolean, showLetters: Boolean) {
        itemView.setOnTouchListener { v, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    onClick.invoke(adapterPosition)
                    v.isPressed = true
                }
                event.action == MotionEvent.ACTION_UP -> {
                    v.isPressed = false
                }
            }

            gestureDetector.onTouchEvent(event)
        }

        numpad_number_txt.visible = false
        numpad_letters_txt.visible = false
        numpad_backspace_img.visible = false
        numpad_fingerprint_img.visible = false

        when (item.type) {
            NumPadItemType.DELETE -> {
                itemView.background = null
                numpad_backspace_img.visible = true
            }

            NumPadItemType.NUMBER -> {
                numpad_number_txt.visible = true
                numpad_letters_txt.visible = item.number != 0 && showLetters
                numpad_number_txt.text = item.number.toString()
                numpad_letters_txt.text = item.letters
            }

            NumPadItemType.FINGER -> {
                itemView.background = null
                numpad_fingerprint_img.visible = isFingerprintEnabled
            }

            NumPadItemType.DOT -> {
                itemView.background = null
                numpad_number_txt.text = "."
                numpad_number_txt.visible = true
            }
        }
    }
}