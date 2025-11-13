package com.example.dictionaryapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "crypto_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        private val LAST_SAVE_TIME = longPreferencesKey("last_save_time")
    }

    suspend fun saveTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SAVE_TIME] = timestamp
        }
    }

    fun getTimestamp(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_SAVE_TIME]
        }
    }
}