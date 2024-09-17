package com.example.voicechanger.viewmodel

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.getDuration
import com.example.voicechanger.util.getSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoiceChangerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer
) : BaseViewModel() {

    private var tempFileName = ""
    private var tempOutputFileName = ""
    private var mergedOutputFileName = ""
    private var finalFileName = ""

    private val outputDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constants.Directories.VOICE_CHANGER_DIR
    )

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isVolumeOn = MutableLiveData(true)
    val isVolumeOn: LiveData<Boolean> = _isVolumeOn

    private val _playbackSpeed = MutableLiveData(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val _isPlaying = MutableLiveData(true)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _maxDuration = MutableLiveData(0)
    val maxDuration: LiveData<Int> = _maxDuration

    private val playbackSpeeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)
    private var currentFileNamePlay = tempFileName

    init {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }

    fun setTempFileName(fileName: String) {
        tempFileName = fileName
        currentFileNamePlay = fileName
        tempOutputFileName = "${tempFileName.replace(".mp3", "")}_temp.mp3"
        mergedOutputFileName = "${tempFileName.replace(".mp3", "")}_merged.mp3"
    }

    fun setFinalFileName(fileName: String) {
        finalFileName = "${outputDir.absolutePath}/${fileName}.mp3"
    }

    fun start() {
        mediaPlayer.apply {
            try {
                reset()
                setDataSource(currentFileNamePlay)
                prepare()
                isLooping = true
                start()
                viewModelScope.launch {
                    updateProgressBar()
                }
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    fun getMaxDuration(): Int {
        val duration = mediaPlayer.duration / 1000
        _maxDuration.postValue(duration)
        return duration
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _isPlaying.value = false
        }
    }

    fun continuePlayback() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            viewModelScope.launch {
                updateProgressBar()
            }
            _isPlaying.value = true
        }
    }

    fun reset() {
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
        _isPlaying.value = true
    }

    private fun setPlaybackSpeed(speed: Float) {
        mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(speed)
        _playbackSpeed.value = speed
    }

    fun cyclePlaybackSpeed() {
        val currentSpeed = _playbackSpeed.value ?: 1.0f
        val nextSpeed =
            playbackSpeeds[(playbackSpeeds.indexOf(currentSpeed) + 1) % playbackSpeeds.size]
        setPlaybackSpeed(nextSpeed)
    }

    fun toggleVolume() {
        val isCurrentlyOn = _isVolumeOn.value ?: true
        _isVolumeOn.value = !isCurrentlyOn
        if (isCurrentlyOn) {
            mediaPlayer.setVolume(0f, 0f)
        } else {
            mediaPlayer.setVolume(1f, 1f)
        }
    }

    private suspend fun updateProgressBar() {
        while (mediaPlayer.isPlaying) {
            val currentPosition = mediaPlayer.currentPosition
            _progress.postValue(currentPosition / 1000)
            delay(100)
        }
        val currentPosition = mediaPlayer.currentPosition
        _progress.postValue(currentPosition / 1000)
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
        _progress.postValue(position / 1000)
    }

    private fun executeFFMPEG(cmd: Array<String>) {
        FFmpeg.execute(cmd)
        val rc = Config.getLastReturnCode()
        val output = Config.getLastCommandOutput()

        when (rc) {
            Config.RETURN_CODE_SUCCESS -> {
                Log.i("GetInfo", "Command execution completed successfully.")
                hideLoading()
                start()
                getMaxDuration()
            }

            Config.RETURN_CODE_CANCEL -> {
                Log.i("GetInfo", "Command execution cancelled by user.")
            }

            else -> {
                Log.i(
                    "GetInfo",
                    String.format(
                        "Command execution failed with rc=%d and output=%s.",
                        rc,
                        output
                    )
                )
            }
        }
    }

    fun applyEffect(effectId: Int) {
        showLoading()
        mediaPlayer.stop()
        val filter = when (effectId) {
            Constants.VoiceEffects.RADIO -> "atempo=1"
            Constants.VoiceEffects.CHIPMUNK -> "asetrate=22100,atempo=1/2"
            Constants.VoiceEffects.ROBOT -> "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4"
            Constants.VoiceEffects.CAVE -> "aecho=0.8:0.9:1000:0.3"
            else -> null
        }
        val cmd = if (filter != null) {
            arrayOf("-y", "-i", tempFileName, "-af", filter, tempOutputFileName)
        } else {
            arrayOf("-y", "-i", tempFileName, tempOutputFileName)
        }
        currentFileNamePlay = tempOutputFileName
        executeFFMPEG(cmd)
    }

    fun mergeWithBackground(backgroundFileName: String, backgroundVolume: Int) {
        showLoading()
        val cmd = if (backgroundFileName.isEmpty()) {
            arrayOf(
                "-y",
                "-i", tempOutputFileName,
                mergedOutputFileName
            )
        } else {
            arrayOf(
                "-y",
                "-i",
                tempOutputFileName,
                "-i",
                backgroundFileName,
                "-filter_complex",
                "[1:a]volume=${backgroundVolume}[bg];[0:a][bg]amix=inputs=2:duration=first:dropout_transition=2",
                mergedOutputFileName
            )
        }
        executeFFMPEG(cmd)
    }

    fun stopMediaPlayer() {
        mediaPlayer.stop()
    }

    fun saveAudio() {
        val tempFile = File(currentFileNamePlay)
        val finalFile = File(finalFileName)

        if (tempFile.exists()) {
            tempFile.copyTo(finalFile, overwrite = true)
            Log.i(TAG, "Processed audio saved to permanent storage.")
        } else {
            Log.e(TAG, "Temporary file does not exist.")
        }
    }

    fun deleteAllTempFiles() {
        val tempFile = File(tempOutputFileName)
        val mergedFile = File(mergedOutputFileName)
        if (tempFile.exists()) {
            tempFile.delete()
        }
        if (mergedFile.exists()) {
            mergedFile.delete()
        }
    }

    fun getAudioSaved() : AudioFile {
        val file = File(finalFileName)
        val duration = file.getDuration()
        val size = file.getSize()
        return AudioFile(file.name, duration, size, file.absolutePath, file.lastModified())
    }

    companion object {
        const val TAG = "VoiceChangerViewModel"
    }
}