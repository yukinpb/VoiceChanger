package com.example.voicechanger.viewmodel

import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.toDurationString
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File

class MainViewModel : BaseViewModel() {

    private val _audioFiles = MutableLiveData<List<AudioFile>>()
    val audioFiles: LiveData<List<AudioFile>> get() = _audioFiles

    init {
        loadAudioFiles()
    }

    private fun loadAudioFiles() {
        val audioFilesList = mutableListOf<AudioFile>()
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val voiceChangerDir = File(downloadDir, Constants.VOICE_CHANGER_DIR)

        if (voiceChangerDir.exists() && voiceChangerDir.isDirectory) {
            val files = voiceChangerDir.listFiles { _, name -> name.endsWith(".mp3") }?.take(5)
            files?.forEach { file ->
                val size = file.length() / (1024 * 1024)
                val duration = getDuration(file)
                audioFilesList.add(AudioFile(file.name, duration, "$size MB"))
            }
        }

        _audioFiles.postValue(audioFilesList)
    }

    private fun getDuration(file: File): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationInMillis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        retriever.release()
        return durationInMillis.toDurationString()
    }
}