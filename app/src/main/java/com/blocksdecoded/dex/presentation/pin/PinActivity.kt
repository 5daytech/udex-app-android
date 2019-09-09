package com.blocksdecoded.dex.presentation.pin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.security.fingerprint.FingerprintAuthenticationDialogFragment
import com.blocksdecoded.dex.core.ui.CoreActivity
import com.blocksdecoded.dex.presentation.main.MainActivity
import com.blocksdecoded.dex.presentation.widgets.MainToolbar
import com.blocksdecoded.dex.presentation.widgets.NumPadItem
import com.blocksdecoded.dex.presentation.widgets.NumPadItemType.*
import com.blocksdecoded.dex.presentation.widgets.NumPadItemsAdapter
import com.blocksdecoded.dex.utils.ui.ToastHelper
import com.blocksdecoded.dex.utils.visible
import kotlinx.android.synthetic.main.activity_pin.*

class PinActivity : CoreActivity(), NumPadItemsAdapter.Listener, FingerprintAuthenticationDialogFragment.Callback {

    private lateinit var viewModel: PinViewModel
    private lateinit var pagesAdapter: PinPagesAdapter

    private val visiblePage: Int?
        get() = (pin_pages_recycler?.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()

    //region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        initViews()

        initViewModel()
    }

    private fun initViews() {
        pagesAdapter = PinPagesAdapter()
        pin_pages_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(pin_pages_recycler)

        pin_pages_recycler.adapter = pagesAdapter
        pin_pages_recycler.setOnTouchListener { _, _ -> true }

        pin_numpad?.bind(this, FINGER, false)
    }

    private fun initViewModel() {
        val interactionType = intent.getSerializableExtra(EXTRA_INTERACTION_TYPE) as PinInteractionType
        val showCancelButton = intent.getBooleanExtra(EXTRA_SHOW_CANCEL, false)

        viewModel = ViewModelProviders.of(this).get(PinViewModel::class.java)
        viewModel.init(interactionType, showCancelButton)

        viewModel.hideToolbar.observe(this, Observer {
            toolbar?.visible = false
        })

        viewModel.showBackButton.observe(this, Observer {
            if (it) {
                toolbar?.bind(MainToolbar.ToolbarState.BACK) {
                    viewModel.onBackPressed()
                }
            }
        })

        viewModel.titleLiveData.observe(this, Observer { title ->
            title?.let {
                toolbar.setTitle(it)
            }
        })

        viewModel.pagesLiveData.observe(this, Observer { pinPages ->
            pinPages?.let {
                pagesAdapter.setPages(pinPages)
            }
        })

        viewModel.showPageAtIndex.observe(this, Observer { index ->
            index?.let {
                Handler().postDelayed({
                    visiblePage?.let {
                        pagesAdapter.setEnteredPinLength(it, 0)
                        pin_pages_recycler?.smoothScrollToPosition(index)
                    }
                }, 300)
            }
        })

        viewModel.showErrorForPage.observe(this, Observer { errorForPage ->
            errorForPage?.let { (error, pageIndex) ->
                pagesAdapter.setErrorForPage(pageIndex, error.let { getString(error) } ?: null)
            }
        })

        viewModel.showError.observe(this, Observer { error ->
            error?.let { ToastHelper.showErrorMessage(it) }
        })

        viewModel.showSuccess.observe(this, Observer { success ->
            success?.let { ToastHelper.showSuccessMessage(it, 1000) }
        })

        viewModel.navigateToMain.observe(this, Observer {
            MainActivity.start(this)
            finish()
        })

        viewModel.fillPinCircles.observe(this, Observer { pair ->
            pair?.let { (length, pageIndex) ->
                pagesAdapter.setEnteredPinLength(pageIndex, length)
            }
        })

        viewModel.dismissWithCancelEvent.observe(this, Observer {
            setResult(RESULT_CANCELLED)
            finish()
        })

        viewModel.dismissWithSuccessEvent.observe(this, Observer {
            setResult(RESULT_OK)
            finish()
        })

        viewModel.showFingerprintInputEvent.observe(this, Observer { cryptoObject ->
            cryptoObject?.let {
                showFingerprintDialog(it)
                pin_numpad.showFingerPrintButton = true
            }
        })

        viewModel.resetCirclesWithShakeAndDelayForPageEvent.observe(this, Observer { pageIndex ->
            pageIndex?.let {
                pagesAdapter.shakePageIndex = it
                pagesAdapter.notifyDataSetChanged()
                Handler().postDelayed({
                    pagesAdapter.shakePageIndex = null
                    pagesAdapter.setEnteredPinLength(pageIndex, 0)
                    viewModel.resetPin()
                }, 300)
            }
        })

        viewModel.closeApplicationEvent.observe(this, Observer {
            finishAffinity()
        })
    }

    override fun addTestLabel() = Unit

    //endregion

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

    //region Biometric

    override fun onFingerprintAuthSucceed() {
        viewModel.onBiometricUnlock()
    }

    private fun showFingerprintDialog(cryptoObject: FingerprintManagerCompat.CryptoObject) {
        val fragment = FingerprintAuthenticationDialogFragment()
        fragment.setCryptoObject(cryptoObject)
        fragment.setCallback(this@PinActivity)
        fragment.isCancelable = true
        fragment.show(supportFragmentManager, "fingerprint_dialog")
    }

    //endregion

    companion object {
        private const val EXTRA_INTERACTION_TYPE = "interaction_type"
        private const val EXTRA_SHOW_CANCEL = "show_cancel"

        const val RESULT_OK = 1
        const val RESULT_CANCELLED = 2

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

