package com.fridaytech.dex.utils.ui

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrUtils {
    fun getBarcode(source: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        return try {
            val imageSize = DimenUtils.dp(150F)
            val bitMatrix = multiFormatWriter.encode(
                source,
                BarcodeFormat.QR_CODE,
                imageSize,
                imageSize,
                hashMapOf(EncodeHintType.MARGIN to 0)
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}
