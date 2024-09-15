package com.example.voicechanger.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.R
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VoiceRecorderViewModel @Inject constructor(
    private val mediaRecorder: MediaRecorder,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _volumeLevel = MutableLiveData<Float>()
    val volumeLevel: LiveData<Float> = _volumeLevel

    private var startTime = 0L
    private var elapsedTime = 0L
    private var isRecording = false
    private var recordingFilePath: String? = null

    fun startRecording() {
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.Directories.VOICE_RECORDER_DIR)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        recordingFilePath = "${outputDir.absolutePath}/recording_${System.currentTimeMillis()}.mp3"

        mediaRecorder.apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordingFilePath)
            prepare()
            start()
        }

        startTime = System.currentTimeMillis()
        isRecording = true
        updateTimer()
        updateVolumeLevel()
    }

    fun pauseRecording() {
        if (isRecording) {
            mediaRecorder.pause()
            elapsedTime += System.currentTimeMillis() - startTime
            isRecording = false
        }
    }

    fun continueRecording() {
        if (!isRecording) {
            mediaRecorder.resume()
            startTime = System.currentTimeMillis()
            isRecording = true
            updateTimer()
            updateVolumeLevel()
        }
    }

    fun resetRecording() {
        mediaRecorder.reset()
        _timerText.value = context.getString(R.string.timer_start)
        _volumeLevel.value = 0f
        elapsedTime = 0L
        isRecording = false
    }

    fun stopRecording() {
        if (isRecording) {
            mediaRecorder.stop()
            isRecording = false
        }
    }

    fun getRecordingFilePath(): String? {
        return recordingFilePath
    }

    private fun updateTimer() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isRecording) {
                val currentTime = System.currentTimeMillis()
                val totalElapsedTime = elapsedTime + (currentTime - startTime)
                val seconds = (totalElapsedTime / 1000) % 60
                val minutes = (totalElapsedTime / (1000 * 60)) % 60
                _timerText.value = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds)
                delay(1000)
            }
        }
    }

    private fun updateVolumeLevel() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isRecording) {
                val maxAmplitude = mediaRecorder.maxAmplitude
                val volume = maxAmplitude / 32767f
                _volumeLevel.postValue(volume)
                delay(100)
            }
        }
    }
}