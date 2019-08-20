package com.blocksdecoded.dex.utils.ui

import android.util.DisplayMetrics
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.view.WindowManager
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.fragment.app.FragmentActivity
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur


object BlurUtils {
    private const val WIDTH_INDEX = 0
    private const val HEIGHT_INDEX = 1

    fun blur(activity: FragmentActivity): Bitmap {
        val bitmap = takeScreenShot(activity)

        val renderScript = RenderScript.create(activity)

        // This will blur the bitmapOriginal with a radius of 16 and save it in bitmapOriginal
        val input = Allocation.createFromBitmap(
            renderScript,
            bitmap
        ) // Use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(25f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        output.destroy()

        return bitmap
    }

    fun takeScreenShot(activity: FragmentActivity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val widthHeight = getScreenSize(activity)
        val bitmapResult = Bitmap.createBitmap(
            bitmap,
            0,
            statusBarHeight,
            widthHeight[0],
            widthHeight[1] - statusBarHeight
        )
        view.destroyDrawingCache()
        return bitmapResult
    }

    fun getScreenSize(context: Context): IntArray {
        val widthHeight = IntArray(2)
        widthHeight[WIDTH_INDEX] = 0
        widthHeight[HEIGHT_INDEX] = 0

        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay

        val size = Point()
        display.getSize(size)
        widthHeight[WIDTH_INDEX] = size.x
        widthHeight[HEIGHT_INDEX] = size.y

        if (!isScreenSizeRetrieved(widthHeight)) {
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            widthHeight[0] = metrics.widthPixels
            widthHeight[1] = metrics.heightPixels
        }

        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.width // deprecated
            widthHeight[1] = display.height // deprecated
        }

        return widthHeight
    }

    private fun isScreenSizeRetrieved(widthHeight: IntArray): Boolean {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0
    }
}