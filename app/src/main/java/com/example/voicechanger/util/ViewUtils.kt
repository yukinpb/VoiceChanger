package com.example.voicechanger.util

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voicechanger.util.ViewUtils.lastTimeClick

object ViewUtils {
    var lastTimeClick = 0L
}

fun View.setOnSafeClickListener(minClickInterval: Long = Constants.Timing.DURATION_TIME_CLICKABLE, onClick: () -> Unit) {
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

fun Activity.toastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}