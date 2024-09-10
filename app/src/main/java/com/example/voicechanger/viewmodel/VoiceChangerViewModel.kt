package com.example.voicechanger.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoiceChangerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var fileName: String = "${context.filesDir}/audioRecord.mp3"
    private var fileNameNew: String = "${context.filesDir}/audioRecordNew.mp3"
    private var isEffectAddedOnce = false

    private val _isProgressVisible = MutableLiveData<Boolean>()
    val isProgressVisible: LiveData<Boolean> get() = _isProgressVisible

    init {
        _isProgressVisible.value = false
    }

    private fun start() {
        mediaPlayer.apply {
            try {
                reset()
                setDataSource(fileNameNew)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
            }
        }
    }

    private fun executeFFMPEG(cmd: Array<String>) {
        FFmpeg.execute(cmd)
        val rc = Config.getLastReturnCode()
        val output = Config.getLastCommandOutput()

        when (rc) {
            Config.RETURN_CODE_SUCCESS -> {
                Log.i("GetInfo", "Command execution completed successfully.")
                hideProgress()
                isEffectAddedOnce = true
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

    fun playRadio() {
        showProgress()
        mediaPlayer.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName,
            "-af",
            "atempo=1",
            fileNameNew
        )
        executeFFMPEG(cmd)
    }

    fun playChipmunk() {
        showProgress()
        mediaPlayer.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName,
            "-af",
            "asetrate=22100,atempo=1/2",
            fileNameNew
        )
        executeFFMPEG(cmd)
    }

    fun playRobot() {
        showProgress()
        mediaPlayer.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName,
            "-af",
            "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4",
            fileNameNew
        )
        executeFFMPEG(cmd)
    }

    fun playCave() {
        showProgress()
        mediaPlayer.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName,
            "-af",
            "aecho=0.8:0.9:1000:0.3",
            fileNameNew
        )
        executeFFMPEG(cmd)
    }

    private fun showProgress() {
        _isProgressVisible.value = true
    }

    private fun hideProgress() {
        _isProgressVisible.value = false
    }

    companion object {
        const val TAG = "VoiceChangerViewModel"
    }
}