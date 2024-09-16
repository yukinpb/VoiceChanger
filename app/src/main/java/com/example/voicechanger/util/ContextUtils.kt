package com.example.voicechanger.util

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.voicechanger.R
import com.example.voicechanger.activity.MainActivity
import com.example.voicechanger.activity.VoiceRecorderActivity
import java.io.File

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

fun Context.goToRecordingActivity() {
    val intent = Intent(this, VoiceRecorderActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

fun Context.shareFile(filePath: String) {
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(
        this,
        "${this.packageName}.provider",
        file
    )
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "audio/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, this.getString(R.string.share_audio)))
}