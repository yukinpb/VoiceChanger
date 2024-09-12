package com.example.voicechanger.activity

import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voicechanger.R
import com.example.voicechanger.databinding.ActivityVoiceRecorderBinding
import com.example.voicechanger.viewmodel.VoiceRecorderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceRecorderActivity : AppCompatActivity() {

    private lateinit var startPauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var resetButton: ImageButton
    private lateinit var timerText: TextView
    private lateinit var recordingStatus: TextView
    private lateinit var tapToRecordHint: TextView

    private var isRecording = false
    private var isPaused = false
    private var recorder: MediaRecorder? = null
    private var timeInSeconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            timeInSeconds++
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            timerText.text = String.format("%02d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        startPauseButton = findViewById(R.id.startPauseButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)
        timerText = findViewById(R.id.timerText)
        recordingStatus = findViewById(R.id.recordingStatus)
        tapToRecordHint = findViewById(R.id.tapToRecordHint)

        // Set button listeners
        startPauseButton.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else if (isPaused) {
                resumeRecording()
            } else {
                pauseRecording()
            }
        }

        stopButton.setOnClickListener {
            stopRecording()
        }

        resetButton.setOnClickListener {
            resetRecording()
        }
    }

    private fun startRecording() {
        // Start MediaRecorder and update UI
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile("path_to_your_file.3gp")
            prepare()
            start()
        }
        isRecording = true
        isPaused = false
        startPauseButton.setBackgroundResource(R.mipmap.ic_pause)
        tapToRecordHint.visibility = View.GONE
        recordingStatus.visibility = View.VISIBLE
        recordingStatus.text = "Recording..."
        handler.post(timerRunnable)
    }

    private fun pauseRecording() {
        isPaused = true
        startPauseButton.setBackgroundResource(R.mipmap.ic_start)
        recordingStatus.text = "Paused"
        handler.removeCallbacks(timerRunnable)
    }

    private fun resumeRecording() {
        isPaused = false
        startPauseButton.setBackgroundResource(R.mipmap.ic_pause)
        recordingStatus.text = "Recording..."
        handler.post(timerRunnable)
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        isPaused = false
        handler.removeCallbacks(timerRunnable)
        timeInSeconds = 0
        // Chuyển sang màn hình khác hoặc xử lý lưu trữ
    }

    private fun resetRecording() {
        // Reset lại mọi thứ
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        isPaused = false
        handler.removeCallbacks(timerRunnable)
        timeInSeconds = 0
        timerText.text = "00:00"
        startPauseButton.setBackgroundResource(R.mipmap.ic_start)
        tapToRecordHint.visibility = View.VISIBLE
        recordingStatus.visibility = View.GONE
    }
}