package com.example.voicechanger.util

import android.content.Context
import android.util.TypedValue
import android.widget.Toast

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.dp2Px(dp: Float) = TypedValue
    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()