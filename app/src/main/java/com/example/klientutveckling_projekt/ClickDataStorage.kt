package com.example.klientutveckling_projekt

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Dataklass som ger tillgång till persistent lagring
 *
 * Använder nyckeln "clicks"
 */
val Context.dataStore by preferencesDataStore(
    name = "clicks"
)
