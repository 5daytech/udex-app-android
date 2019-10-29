package com.blocksdecoded.dex.presentation.dialogs

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.annotation.NonNull
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.getColorRes
import com.blocksdecoded.dex.utils.ui.BlurUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

abstract class BaseBottomDialog(
    private val layoutId: Int
) : BottomSheetDialogFragment() {
    private var backgroundDrawable: BitmapDrawable? = null
    private val backgroundBlurEnabled = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val theme = if (App.appPreferences.isLightModeEnabled)
            R.style.LightBottomSheet
        else
            R.style.DarkBottomSheet

        val dialog = activity?.let {
            BottomSheetDialog(it, theme)
        }

        requireNotNull(dialog)

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.isFitToContents = true

            if (backgroundBlurEnabled) {
                setBehaviorListener(bottomSheetBehavior)

                Handler().postDelayed({
                    activity?.let {
                        val blurredBackground = BlurUtils.blur(it)
                        backgroundDrawable = BitmapDrawable(context?.resources, blurredBackground)
                        val anim = ObjectAnimator.ofInt(0, 255)
                        dialog.window?.setBackgroundDrawable(backgroundDrawable)
                        anim.duration = 500L
                        anim.addUpdateListener {
                            (it.animatedValue as Int).let {
                                backgroundDrawable?.alpha = it
                            }
                        }
                        anim.start()
                    }
                }, 200)
            }
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutId, container, false)

    private fun resetBackground() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(context?.getColorRes(R.color.transparent) ?: 0))
        backgroundDrawable?.alpha = 0
        backgroundDrawable = null
    }

    private fun setBehaviorListener(bottomSheetBehavior: BottomSheetBehavior<View?>?) {
        bottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> dismiss()
                    BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                try {
                    if (!slideOffset.isNaN()) {
                        backgroundDrawable?.alpha = (255 * (1 + slideOffset)).roundToInt()
                    }
                } catch (e: Exception) {
                    Logger.e(e)
                }
            }
        })
    }
}
