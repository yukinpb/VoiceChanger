package com.example.voicechanger.util

import android.os.SystemClock
import android.view.View

private var lastTimeClick = 0L
private const val MIN_CLICK_INTERVAL = 1000L

fun View.setOnSafeClickListener(minClickInterval: Long = MIN_CLICK_INTERVAL, onClick: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            if (SystemClock.elapsedRealtime() - lastTimeClick < minClickInterval) {
                return
            }
            lastTimeClick = SystemClock.elapsedRealtime()
            onClick()
        }
    })
}