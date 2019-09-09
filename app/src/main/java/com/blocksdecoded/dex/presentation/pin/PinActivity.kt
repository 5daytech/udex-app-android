package com.blocksdecoded.dex.presentation.pin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType.*
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.dialog_send.*

class PinActivity : CoreActivity(), NumPadItemsAdapter.Listener {

    private lateinit var viewModel: PinViewModel
    private lateinit var pagesAdapter: PinPagesAdapter

    private val visiblePage: Int?
        get() = (pin_pages_recycler?.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        pagesAdapter = PinPagesAdapter()
        pin_pages_recycler.layoutManager = LinearLayoutManager(this)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(pin_pages_recycler)

        pin_pages_recycler.adapter = pagesAdapter
        pin_pages_recycler.setOnTouchListener { _, _ -> true }

        val interactionType = intent.getSerializableExtra(EXTRA_INTERACTION_TYPE) as PinInteractionType
        val showCancelButton = intent.getBooleanExtra(EXTRA_SHOW_CANCEL, false)

        viewModel = ViewModelProviders.of(this).get(PinViewModel::class.java)
        viewModel.init(interactionType, showCancelButton)

        send_numpad?.bind(this, FINGER, false)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onItemClick(item: NumPadItem) {
        visiblePage?.let {  page ->
            when (item.type) {
                NUMBER -> viewModel.onNumberEnter(page, item.number.toString())
                DELETE -> viewModel.onDeleteClick(page)
                FINGER -> viewModel.onBiometricUnlockClick()
                else -> {}
            }
        }
    }

    companion object {
        private const val EXTRA_INTERACTION_TYPE = "interaction_type"
        private const val EXTRA_SHOW_CANCEL = "show_cancel"

        const val RESULT_OK = 1
        const val RESULT_CANCELLED = 2
        const val PIN_COUNT = 6

        fun startForSetPin(context: AppCompatActivity, requestCode: Int) {
            startForResult(context, requestCode, PinInteractionType.SET_PIN)
        }

        fun startForEditPin(context: Context) {
            start(context, PinInteractionType.EDIT_PIN)
        }

        fun startForUnlock(context: AppCompatActivity, requestCode: Int, showCancel: Boolean = false) {
            startForResult(context, requestCode, PinInteractionType.UNLOCK, showCancel)
        }

        private fun start(context: Context, interactionType: PinInteractionType) {
            val intent = Intent(context, PinActivity::class.java)
            intent.putExtra(EXTRA_INTERACTION_TYPE, interactionType)
            context.startActivity(intent)
        }

        private fun startForResult(context: AppCompatActivity, requestCode: Int, interactionType: PinInteractionType, showCancel: Boolean = true) {
            val intent = Intent(context, PinActivity::class.java)
            intent.putExtra(EXTRA_INTERACTION_TYPE, interactionType)
            intent.putExtra(EXTRA_SHOW_CANCEL, showCancel)
            context.startActivityForResult(intent, requestCode)
        }
    }
}

