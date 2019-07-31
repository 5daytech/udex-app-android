package com.blocksdecoded.dex.utils.ui

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrUtils {
    fun getBarcode(source: String): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(source, BarcodeFormat.QR_CODE, 400, 400)
    }
}