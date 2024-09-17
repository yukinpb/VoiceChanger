package com.example.voicechanger.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.voicechanger.base.pref.BasePreferencesImpl
import com.example.voicechanger.model.Language
import com.example.voicechanger.util.LanguageProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) : BasePreferencesImpl(context) {

    private object PreferencesKeys {
        val PREF_PARAM_LANGUAGE = stringPreferencesKey("PREF_PARAM_LANGUAGE")
        val PREF_PARAM_SHOW_INTRODUCE = booleanPreferencesKey("PREF_PARAM_SHOW_INTRODUCE")
    }

    fun getLanguage(): Flow<Language?> =
        getValue(PreferencesKeys.PREF_PARAM_LANGUAGE).map { languageCode ->
            languageCode?.let { code ->
                LanguageProvider.getLanguages().find { it.locale.toLanguageTag() == code }
            } ?: LanguageProvider.getLanguages().find { it.locale == Locale.US }
        }

    suspend fun setLanguage(language: Language) {
        putValue(PreferencesKeys.PREF_PARAM_LANGUAGE, language.locale.toLanguageTag())
    }

    fun shouldShowIntroduce(): Flow<Boolean?> = getValue(PreferencesKeys.PREF_PARAM_SHOW_INTRODUCE)

    suspend fun setShouldShowIntroduce(show: Boolean) {
        putValue(PreferencesKeys.PREF_PARAM_SHOW_INTRODUCE, show)
    }
}