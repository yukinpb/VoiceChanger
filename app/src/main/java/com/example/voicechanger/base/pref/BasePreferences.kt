package com.example.voicechanger.base.pref

import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.Preferences

interface BasePreferences {

    fun <T> getValue(key: Preferences.Key<T>): Flow<T?>
    suspend fun <T> putValue(key: Preferences.Key<T>, value: T)

    suspend fun clear()

    suspend fun remove(key: Preferences.Key<*>)
}