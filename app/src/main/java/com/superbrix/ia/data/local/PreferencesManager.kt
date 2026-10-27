package com.superbrix.ia.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "brix360_preferences")

/**
 * Gestor de persistencia en DataStore para configuraciones de BRIX360.
 */
class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("key_dark_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("key_notifications")
        val KEY_VOICE_ASSIST = booleanPreferencesKey("key_voice_assist")
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: true // Modo oscuro por defecto
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS] ?: true
    }

    val voiceEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_VOICE_ASSIST] ?: true
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_VOICE_ASSIST] = enabled
        }
    }
}
