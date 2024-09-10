package com.example.voicechanger.viewmodel

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.CountDownTimer
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
    private var fileName: String = "${context.filesDir}/audioRecord.mp3"
    private lateinit var countDownTimer: CountDownTimer
    private var permissions: Array<String> =
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> get() = _isRecording

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText

    init {
        _isRecording.value = false
        _timerText.value = "30"
    }

    fun requestAudioPermissions(activity: VoiceRecorderActivity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == -1 ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == -1 ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1
        ) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    fun startRecording() {
        _isRecording.value = true
        _timerText.value = "30"
        countDownTimer = object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerText.value = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {}
        }
        countDownTimer.start()

        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(fileName)
                prepare()
                start()
            }
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "start() failed")
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        countDownTimer.cancel()

        try {
            mediaRecorder.apply {
                stop()
                release()
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "stop() failed: ${e.message}")
        }
    }

    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        const val TAG = "VoiceRecorderViewModel"
    }
}