package com.blocksdecoded.dex.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R

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
    interface Listener {
        fun onItemClick(item: NumPadItem)
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NumPadItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_numpad_button, parent, false))
    }

    override fun getItemCount() = numPadItems.count()

    var showFingerPrintButton = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NumPadItemViewHolder) {
            holder.bind(numPadItems[position], showFingerPrintButton, showLetters) { listener.onItemClick(numPadItems[position]) }
        }
    }
}

data class NumPadItem(val type: NumPadItemType, val number: Int, val letters: String)

enum class NumPadItemType {
    NUMBER, DELETE, FINGER, DOT
}

class NumPadItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var txtNumber: TextView = itemView.findViewById(R.id.numpad_number_txt)
    private var txtLetters: TextView = itemView.findViewById(R.id.numpad_letters_txt)
    private var imgBackSpace: ImageView = itemView.findViewById(R.id.numpad_backspace_img)
    private var imgFingerprint: ImageView = itemView.findViewById(R.id.numpad_fingerprint_img)

    fun bind(item: NumPadItem, isFingerprintEnabled: Boolean, showLetters: Boolean, onClick: () -> (Unit)) {
        itemView.setOnTouchListener { v, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    onClick.invoke()
                    v.isPressed = true
                    true
                }
                event.action == MotionEvent.ACTION_UP -> {
                    v.isPressed = false
                    true
                }
                else -> false
            }
        }

        txtNumber.visibility = View.GONE
        txtLetters.visibility = View.GONE
        imgBackSpace.visibility = View.GONE
        imgFingerprint.visibility = View.GONE

        when (item.type) {
            NumPadItemType.DELETE -> {
                itemView.background = null
                imgBackSpace.visibility = View.VISIBLE
            }

            NumPadItemType.NUMBER -> {
                txtNumber.visibility = View.VISIBLE
                txtLetters.visibility = if (item.number == 0 || !showLetters) View.GONE else View.VISIBLE
                txtNumber.text = item.number.toString()
                txtLetters.text = item.letters
            }

            NumPadItemType.FINGER -> {
                itemView.background = null
                imgFingerprint.visibility = if (isFingerprintEnabled) View.VISIBLE else View.GONE
            }

            NumPadItemType.DOT -> {
                itemView.background = null
                txtNumber.text = "."
                txtNumber.visibility = View.VISIBLE
            }

        }
    }
}