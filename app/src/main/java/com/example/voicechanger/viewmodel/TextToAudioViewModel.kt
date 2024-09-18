package com.example.voicechanger.viewmodel

import android.app.Application
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.base.viewmodel.BaseViewModel
import com.example.voicechanger.model.Language
import com.example.voicechanger.pref.AppPreferences
import com.example.voicechanger.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TextToAudioViewModel @Inject constructor(
    private val application: Application,
    private val appPreferences: AppPreferences
) : BaseViewModel(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech = TextToSpeech(application, this)
    private var currentText: String = ""

    private val _language = MutableLiveData<Language?>()
    val language: LiveData<Language?> get() = _language

    init {
        viewModelScope.launch {
            appPreferences.getLanguage().collect { language ->
                language?.let {
                    setLanguage(it)
                    _language.postValue(it)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = _language.value?.locale
        }
    }

    fun speakText(text: String) {
        if (text.isNotEmpty()) {
            currentText = text
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun saveAudio(text: String): String {
        val outputDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.Directories.VOICE_RECORDER_DIR)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val filePath = "${outputDir.absolutePath}/tts_${System.currentTimeMillis()}.mp3"
        val file = File(filePath)
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, file.name)
        }
        tts.synthesizeToFile(text, params, file, file.name)
        return filePath
    }

    fun setLanguage(language: Language) {
        tts.language = language.locale
        viewModelScope.launch {
            updateLanguage(language)
        }
        if (currentText.isNotEmpty()) {
            speakText(currentText)
        }
    }

    private suspend fun updateLanguage(language: Language) {
        appPreferences.setLanguage(language)
    }

    override fun onCleared() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onCleared()
    }
}