package com.example.klientutveckling_projekt

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClickRepository(private val context: Context) {

    val clicks: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[ClickKeys.CLICKS] ?: 0
        }

    suspend fun incrementClicks() {
        context.dataStore.edit { preferences ->
            val current = preferences[ClickKeys.CLICKS] ?: 0
            preferences[ClickKeys.CLICKS] = current + 1
        }
    }
}
