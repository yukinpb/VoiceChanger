package com.example.voicechanger.util

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.Toast
import com.example.voicechanger.activity.MainActivity

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.dp2Px(dp: Float) = TypedValue
    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun Context.goToMainActivity() {
    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}