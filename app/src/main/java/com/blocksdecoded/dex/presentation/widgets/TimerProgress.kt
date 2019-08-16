package com.blocksdecoded.dex.presentation.widgets

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.blocksdecoded.dex.R
import java.util.*

class TimerProgress : RelativeLayout {
	
	private val interval = 1000L
	private var countDownTimer: CountDownTimer? = null
	
	private val timerTask = object: TimerTask() {
		override fun run() {
		
		}
	}
	
	init { View.inflate(context, R.layout.view_progress, this) }
	
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
	
	fun bind(
		time: Long
	) {
		countDownTimer?.cancel()
		
		countDownTimer = object : CountDownTimer(interval, time) {
			override fun onFinish() {
			
			}
			
			override fun onTick(p0: Long) {
			
			}
		}
		
		countDownTimer?.start()
	}
}