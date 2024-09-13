package com.example.voicechanger.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.Level
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.toDurationString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class VoiceChangerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private var tempFileName: String = ""
    private var finalFileName: String = ""
    private val outputDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        Constants.VOICE_CHANGER_DIR
    )

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _pitch = MutableLiveData<Int>()
    val pitch: LiveData<Int> = _pitch

    private val _isVolumeOn = MutableLiveData<Boolean>(true)
    val isVolumeOn: LiveData<Boolean> = _isVolumeOn

    private val _playbackSpeed = MutableLiveData<Float>(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val _isPlaying = MutableLiveData<Boolean>(true)
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

    private fun start() {
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

    fun getMaxDuration(): String {
        return mediaPlayer.duration.toLong().toDurationString()
    }

    fun getCurrentPlaybackTime(): String {
        return mediaPlayer.currentPosition.toLong().toDurationString()
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
        val nextSpeed = playbackSpeeds[(playbackSpeeds.indexOf(currentSpeed) + 1) % playbackSpeeds.size]
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
        val duration = mediaPlayer.duration
        val handler = android.os.Handler()
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    _progress.postValue((currentPosition * 100) / duration)
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
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
            else -> throw IllegalArgumentException("Unknown effect ID")
        }
        val cmd = arrayOf("-y", "-i", tempFileName, "-af", filter, tempFileName)
        executeFFMPEG(cmd)
    }

    private fun changePitch(pitch: Float) {
        showLoading()
        mediaPlayer.stop()
        val pitchMultiplier = 2.0.pow(pitch / 12.0).toFloat()
        val filter = "asetrate=44100*$pitchMultiplier,aresample=44100"
        val cmd = arrayOf("-y", "-i", tempFileName, "-af", filter, tempFileName)
        executeFFMPEG(cmd)
    }

    fun getCurrentPitch(): Int {
        val cmd = arrayOf("-i", tempFileName, "-af", "astats=metadata=1:reset=1", "-f", "null", "-")
        val output = StringBuilder()
        var pitchValue = 0

        FFmpeg.executeAsync(cmd) { _, returnCode ->
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                Config.enableLogCallback { logMessage ->
                    if (logMessage.level == Level.AV_LOG_INFO) {
                        output.append(logMessage.text)
                    }
                }

                val pitchRegex = Regex("RMS\\s+level\\s+dB\\s*:\\s*(-?\\d+\\.\\d+)")
                val matchResult = pitchRegex.find(output.toString())
                pitchValue = matchResult?.groupValues?.get(1)?.toFloat()?.toInt() ?: 0
            }
        }

        return pitchValue
    }

    fun setPitch(pitch: Int) {
        _pitch.value = pitch
        changePitch(pitch.toFloat())
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