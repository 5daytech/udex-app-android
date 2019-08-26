package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.blocksdecoded.dex.R
import kotlinx.android.synthetic.main.view_progress.view.*
import java.util.*
import kotlin.math.roundToInt

class TimerProgress : RelativeLayout {
	init { View.inflate(context, R.layout.view_progress, this) }

	private val interval = 100L
	private var countDownTimer: CountDownTimer? = null

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
	
	fun bind(
		seconds: Int,
		onFinish: () -> Unit
	) {
		countDownTimer?.cancel()
		val milliseconds = seconds * 1000L
		progress_bar?.max = milliseconds.toInt()
		
		countDownTimer = object : CountDownTimer(milliseconds, interval) {
			override fun onFinish() {
				onFinish()
			}
			
			override fun onTick(p0: Long) {
				val secondsLeft = seconds - (p0 / 1000).toInt()
				progress_hint?.text = "$secondsLeft:$seconds"

				progress_bar?.progress = (milliseconds - p0).toInt()
			}
		}
		
		countDownTimer?.start()
	}
}