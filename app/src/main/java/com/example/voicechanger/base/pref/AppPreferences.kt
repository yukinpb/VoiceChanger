package com.example.voicechanger.base.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) : BasePreferencesImpl(context){

    private object PreferencesKeys {
        val PREF_PARAM_LANGUAGE = stringPreferencesKey("PREF_PARAM_LANGUAGE")
        val PREF_PARAM_SHOW_INTRODUCE = booleanPreferencesKey("PREF_PARAM_SHOW_INTRODUCE")
    }

    fun getLanguage(): Flow<Locale?> = getValue(PreferencesKeys.PREF_PARAM_LANGUAGE).map { languageCode ->
        languageCode?.let { Locale.forLanguageTag(it) }
    }

    suspend fun setLanguage(locale: Locale) {
        putValue(PreferencesKeys.PREF_PARAM_LANGUAGE, locale.toLanguageTag())
    }

    fun shouldShowIntroduce(): Flow<Boolean?> = getValue(PreferencesKeys.PREF_PARAM_SHOW_INTRODUCE)

    suspend fun setShouldShowIntroduce(show: Boolean) {
        putValue(PreferencesKeys.PREF_PARAM_SHOW_INTRODUCE, show)
    }
}