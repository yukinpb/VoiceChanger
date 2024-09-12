package com.example.voicechanger.viewmodel

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicechanger.activity.VoiceRecorderActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoiceRecorderViewModel @Inject constructor(
    private val mediaRecorder: MediaRecorder,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var timeInSeconds = 0
    private val handler = Handler(Looper.getMainLooper())

    private val _timerText = MutableLiveData("00:00")
    val timerText: LiveData<String> = _timerText

    private val _recordingStatus = MutableLiveData("")
    val recordingStatus: LiveData<String> = _recordingStatus

    private val _showBubbleHint = MutableLiveData(true)
    val showBubbleHint: LiveData<Boolean> = _showBubbleHint

    private var isRecording = false
    private var isPaused = false

    private val timerRunnable = object : Runnable {
        override fun run() {
            timeInSeconds++
            val minutes = timeInSeconds / 60
            val seconds = timeInSeconds % 60
            _timerText.value = String.format("%02d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    fun onStartPauseClicked() {
        if (!isRecording) {
            startRecording()
        } else if (isPaused) {
            resumeRecording()
        } else {
            pauseRecording()
        }
    }

    private fun startRecording() {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile("path_to_your_file.3gp")
            prepare()
            start()
        }
        isRecording = true
        isPaused = false
        _showBubbleHint.value = false
        _recordingStatus.value = "Recording..."
        handler.post(timerRunnable)
    }

    private fun pauseRecording() {
        isPaused = true
        _recordingStatus.value = "Paused"
        handler.removeCallbacks(timerRunnable)
        // Note: `MediaRecorder` does not have a built-in pause method.
    }

    private fun resumeRecording() {
        isPaused = false
        _recordingStatus.value = "Recording..."
        handler.post(timerRunnable)
        // Resume recording logic (e.g., by handling file appending manually if needed)
    }

    fun onStopClicked() {
        mediaRecorder.apply {
            stop()
            reset()
        }
        isRecording = false
        isPaused = false
        handler.removeCallbacks(timerRunnable)
        _recordingStatus.value = "Stopped"
    }

    fun onResetClicked() {
        mediaRecorder.apply {
            stop()
            reset()
        }
        isRecording = false
        isPaused = false
        handler.removeCallbacks(timerRunnable)
        timeInSeconds = 0
        _timerText.value = "00:00"
        _showBubbleHint.value = true
        _recordingStatus.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder.release()
        handler.removeCallbacks(timerRunnable)
    }
}