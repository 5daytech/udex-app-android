package com.blocksdecoded.dex.core.security.fingerprint

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
//source https://github.com/googlesamples/android-FingerprintDialog

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.widget.ImageViewCompat
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.widgets.dialogs.BaseDialog
import com.blocksdecoded.dex.utils.ui.ToastHelper

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
class FingerprintAuthenticationDialogFragment : BaseDialog(R.layout.dialog_fingerprint), FingerprintCallback {

    private lateinit var cancelButton: View
    private lateinit var fingerprintIcon: ImageView
    private lateinit var errorTextView: TextView

    private var callback: Callback? = null
    private var cryptoObject: FingerprintManagerCompat.CryptoObject? = null
    private var authCallbackHandler: FingerprintAuthenticationHandler? = null

     private val ERROR_TIMEOUT_MILLIS: Long = 1900
     private val SUCCESS_DELAY_MILLIS: Long = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        retainInstance = true
        isCancelable = false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton = view.findViewById(R.id.fingerprint_cancel)
        fingerprintIcon = view.findViewById(R.id.fingerprint_icon)
        errorTextView = view.findViewById(R.id.fingerprint_status)

        cancelButton.setOnClickListener { dismiss() }

        authCallbackHandler = cryptoObject?.let { FingerprintAuthenticationHandler(this, it) }
    }

    override fun onPause() {
        super.onPause()
        authCallbackHandler?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        authCallbackHandler?.startListening()

        dialog?.setOnKeyListener { _, keyCode, _ ->
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                // To dismiss the fragment when the back-button is pressed.
                dismiss()
                true
            }
            // Otherwise, do nothing else
            else false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        authCallbackHandler?.releaseFingerprintCallback()
    }

    override fun onAuthenticated() {
        errorTextView.run {
            removeCallbacks(resetErrorTextRunnable)
            setTextColor(errorTextView.resources.getColor(R.color.green, null))
            text = errorTextView.resources.getString(R.string.fingerprint_success)
        }

        fingerprintIcon.run {
            setImageTintColor(fingerprintIcon, R.color.green)
            postDelayed({ callback?.onFingerprintAuthSucceed() }, SUCCESS_DELAY_MILLIS)
        }
    }

    override fun onAuthenticationHelp(helpString: CharSequence?) { }

    override fun onAuthenticationFailed() {
        showError(getString(R.string.fingerprint_not_recognized))
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
        if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
            ToastHelper.showErrorMessage(R.string.error_enter_your_pin)
            dismiss()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setCryptoObject(cryptoObject: FingerprintManagerCompat.CryptoObject) {
        this.cryptoObject = cryptoObject
    }

    private val resetErrorTextRunnable = Runnable {
        errorTextView.run {
            text = errorTextView.resources.getString(R.string.fingerprint_hint)
        }
    }

    private fun showError(error: CharSequence) {
        dialog?.let {
            errorTextView.run {
                text = error
                setTextColor(errorTextView.resources.getColor(R.color.red_warning, null))
                removeCallbacks(resetErrorTextRunnable)
                postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS)
            }

            setImageTintColor(fingerprintIcon, R.color.red_warning)
        }
    }

    private fun setImageTintColor(image: ImageView, colorResource: Int) {
        val color = ContextCompat.getColor(image.context, colorResource)
        ImageViewCompat.setImageTintList(image, ColorStateList.valueOf(color))
    }

    interface Callback {
        fun onFingerprintAuthSucceed()
    }
}
