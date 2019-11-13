package com.fridaytech.dex.presentation.pin

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fridaytech.dex.R
import com.fridaytech.dex.utils.inflate

class PinPage(val description: Int, var enteredDigitsLength: Int = 0, var error: String? = null)

class PinPagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pinPages = mutableListOf<PinPage>()
    var shakePageIndex: Int? = null

    fun setPages(pinPages: List<PinPage>) {
        this.pinPages.addAll(pinPages)
        notifyDataSetChanged()
    }

    fun setErrorForPage(pageIndex: Int, error: String?) {
        pinPages[pageIndex].error = error
        notifyDataSetChanged()
    }

    fun setEnteredPinLength(pageIndex: Int, enteredLength: Int) {
        pinPages[pageIndex].enteredDigitsLength = enteredLength
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PinPageViewHolder(parent.inflate(R.layout.item_pin_page))

    override fun getItemCount() = pinPages.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PinPageViewHolder) {
            holder.bind(pinPages[position], shakePageIndex == position)
        }
    }
}

class PinPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var txtDesc: TextView = itemView.findViewById(R.id.pin_page_description)
    private var txtError: TextView = itemView.findViewById(R.id.pin_page_error)
    private var pinCirclesWrapper: View = itemView.findViewById(R.id.pin_page_circles)

    private var imgPinMask1: ImageView = itemView.findViewById(R.id.pin_page_mask_1)
    private var imgPinMask2: ImageView = itemView.findViewById(R.id.pin_page_mask_2)
    private var imgPinMask3: ImageView = itemView.findViewById(R.id.pin_page_mask_3)
    private var imgPinMask4: ImageView = itemView.findViewById(R.id.pin_page_mask_4)
    private var imgPinMask5: ImageView = itemView.findViewById(R.id.pin_page_mask_5)
    private var imgPinMask6: ImageView = itemView.findViewById(R.id.pin_page_mask_6)

    fun bind(pinPage: PinPage, shake: Boolean) {
        txtDesc.text = itemView.resources.getString(pinPage.description)
        updatePinCircles(pinPage.enteredDigitsLength)
        txtError.text = pinPage.error ?: ""
        if (shake) {
            val shakeAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.shake_pin_circles)
            pinCirclesWrapper.startAnimation(shakeAnim)
        }
    }

    private fun updatePinCircles(length: Int) {
        val filledCircle = R.drawable.ic_filled_circle
        val emptyCircle = R.drawable.ic_empty_circle

        imgPinMask1.setImageResource(if (length > 0) filledCircle else emptyCircle)
        imgPinMask2.setImageResource(if (length > 1) filledCircle else emptyCircle)
        imgPinMask3.setImageResource(if (length > 2) filledCircle else emptyCircle)
        imgPinMask4.setImageResource(if (length > 3) filledCircle else emptyCircle)
        imgPinMask5.setImageResource(if (length > 4) filledCircle else emptyCircle)
        imgPinMask6.setImageResource(if (length > 5) filledCircle else emptyCircle)
    }
}
