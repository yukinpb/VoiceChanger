package com.example.voicechanger.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.model.AudioFile
import com.example.voicechanger.util.Constants
import com.example.voicechanger.util.SortType
import com.example.voicechanger.util.getDuration
import com.example.voicechanger.util.getSize
import java.io.File

class MainViewModel : BaseViewModel() {

    private val _audioFiles = MutableLiveData<List<AudioFile>>()
    val audioFiles: LiveData<List<AudioFile>> get() = _audioFiles

    private var sortType: SortType = SortType.DATE_NEWEST

    fun setSortType(newSortType: SortType) {
        sortType = newSortType
        sortAudioFiles()
    }

    fun getSortType() = sortType

    fun loadAudioFiles() {
        val audioFilesList = mutableListOf<AudioFile>()
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val voiceChangerDir = File(downloadDir, Constants.Directories.VOICE_CHANGER_DIR)

        if (voiceChangerDir.exists() && voiceChangerDir.isDirectory) {
            val files = voiceChangerDir.listFiles { _, name -> name.endsWith(".mp3") }
                ?.sortedByDescending { it.lastModified() }
                ?.take(5)
            files?.forEach { file ->
                val size = file.getSize()
                val duration = file.getDuration()
                audioFilesList.add(AudioFile(file.name, duration, size, file.absolutePath, file.lastModified()))
            }
        }

        _audioFiles.postValue(audioFilesList)
    }

    fun loadAudioFiles(directory: String) {
        val audioFilesList = mutableListOf<AudioFile>()
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val voiceChangerDir = File(downloadDir, directory)

        if (voiceChangerDir.exists() && voiceChangerDir.isDirectory) {
            val files = voiceChangerDir.listFiles { _, name -> name.endsWith(".mp3") }

            files?.forEach { file ->
                val size = file.getSize()
                val duration = file.getDuration()
                audioFilesList.add(AudioFile(file.name, duration, size, file.absolutePath, file.lastModified()))
            }
        }

        _audioFiles.postValue(audioFilesList)
    }

    private fun sortAudioFiles() {
        val sortedList = _audioFiles.value?.let { fileList ->
            when (sortType) {
                SortType.NAME_ASC -> fileList.sortedBy { it.name }
                SortType.NAME_DESC -> fileList.sortedByDescending { it.name }
                SortType.DATE_NEWEST -> fileList.sortedByDescending { it.lastModifier }
                SortType.DATE_OLDEST -> fileList.sortedBy { it.lastModifier }
            }
        } ?: emptyList()

        _audioFiles.postValue(sortedList)
    }
}