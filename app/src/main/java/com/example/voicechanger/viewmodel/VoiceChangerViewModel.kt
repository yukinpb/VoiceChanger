package com.example.voicechanger.viewmodel

import android.media.MediaPlayer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoiceChangerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer
) : BaseViewModel() {

    private var tempFileName: String = ""
    private val tempOutputFileName = "${tempFileName}_temp.mp3"
    private var finalFileName: String = ""
    private val outputDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constants.VOICE_CHANGER_DIR
    )

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isVolumeOn = MutableLiveData(true)
    val isVolumeOn: LiveData<Boolean> = _isVolumeOn

    private val _playbackSpeed = MutableLiveData(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val _isPlaying = MutableLiveData(true)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val playbackSpeeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)

    init {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }

    fun setTempFileName(fileName: String) {
        tempFileName = fileName
    }

    fun setFinalFileName(fileName: String) {
        finalFileName = "${outputDir.absolutePath}/${fileName}.mp3"
    }

    fun start() {
        mediaPlayer.apply {
            try {
                reset()
                setDataSource(tempFileName)
                prepare()
                isLooping = true
                start()
                updateProgressBar()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    fun getMaxDuration(): Int {
        return mediaPlayer.duration / 1000
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

    private fun updateProgressBar() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    _progress.postValue(currentPosition / 1000)
                }
                handler.postDelayed(this, 1000)
            }
        })
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
            Constants.RADIO -> "atempo=1"
            Constants.CHIPMUNK -> "asetrate=22100,atempo=1/2"
            Constants.ROBOT -> "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4"
            Constants.CAVE -> "aecho=0.8:0.9:1000:0.3"
            else -> null
        }
        val cmd = if (filter != null) {
            arrayOf("-y", "-i", tempFileName, "-af", filter, tempOutputFileName)
        } else {
            arrayOf("-y", "-i", tempFileName, tempOutputFileName)
        }
        executeFFMPEG(cmd)
    }

    fun saveAudio() {
        val tempFile = File(tempFileName)
        val finalFile = File(finalFileName)
        if (tempFile.exists()) {
            tempFile.copyTo(finalFile, overwrite = true)
            tempFile.delete()
            Log.i(TAG, "Processed audio saved to permanent storage.")
        } else {
            Log.e(TAG, "Temporary file does not exist.")
        }
    }

    companion object {
        const val TAG = "VoiceChangerViewModel"
    }
}