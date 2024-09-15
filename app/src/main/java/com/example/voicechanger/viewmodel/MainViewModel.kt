package com.example.voicechanger.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.getDuration
import com.example.voicechanger.util.getSize
import java.io.File

class MainViewModel : BaseViewModel() {

    private val _audioFiles = MutableLiveData<List<AudioFile>>()
    val audioFiles: LiveData<List<AudioFile>> get() = _audioFiles

    init {
        loadAudioFiles()
    }

    fun loadAudioFiles() {
        val audioFilesList = mutableListOf<AudioFile>()
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val voiceChangerDir = File(downloadDir, Constants.Directories.VOICE_CHANGER_DIR)

        if (voiceChangerDir.exists() && voiceChangerDir.isDirectory) {
            val files = voiceChangerDir.listFiles { _, name -> name.endsWith(".mp3") }
                ?.sortedByDescending { it.lastModified() }
                ?.take(5)
            files?.forEach { file ->
                val size = file.getSize()
                val duration = file.getDuration()
                audioFilesList.add(AudioFile(file.name, duration, size))
            }
        }

        _audioFiles.postValue(audioFilesList)
    }
}