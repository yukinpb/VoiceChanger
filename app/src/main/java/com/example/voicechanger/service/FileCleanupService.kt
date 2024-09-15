package com.example.voicechanger.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.voicechanger.R
import com.example.voicechanger.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class FileCleanupService : Service() {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var mediaRecorder: MediaRecorder

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.Directories.VOICE_RECORDER_DIR)
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.name.contains("_merged") || file.name.contains("_temp")) {
                    file.delete()
                }
            }
        }

        mediaRecorder.release()
        mediaPlayer.release()

        stopSelf()
    }

    private fun startForegroundService() {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("file_cleanup_service", "Voice Changer")
        } else {
            ""
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Voice Changer is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        chan.lightColor = android.graphics.Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(NotificationManager::class.java)
        service.createNotificationChannel(chan)
        return channelId
    }
}