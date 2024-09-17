package com.example.voicechanger.viewmodel

import android.app.Application
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import androidx.lifecycle.viewModelScope
import com.example.voicechanger.base.pref.AppPreferences
import com.example.voicechanger.base.viewmodel.BaseViewModel
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

    init {
        viewModelScope.launch {
            appPreferences.getLanguage().collect { locale ->
                locale?.let { setLanguage(it) }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
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

    fun resetTextToSpeech() {
        tts.stop()
        tts.shutdown()
        tts = TextToSpeech(application, this)
    }

    fun setLanguage(locale: Locale) {
        tts.language = locale
        viewModelScope.launch {
            updateLanguage(locale)
        }
        if (currentText.isNotEmpty()) {
            speakText(currentText)
        }
    }

    private suspend fun updateLanguage(locale: Locale) {
        appPreferences.setLanguage(locale)
    }

    override fun onCleared() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onCleared()
    }
}