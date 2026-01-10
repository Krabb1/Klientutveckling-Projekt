package com.example.klientutveckling_projekt

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClickRepository(private val context: Context) {

    private object Keys {
        val METERS = doublePreferencesKey("meters")
    }

    val meters: Flow<Double> = context.dataStore.data
        .map { it[Keys.METERS] ?: 0.0 }

    suspend fun addMeters(amount: Double) {
        context.dataStore.edit {
            val current = it[Keys.METERS] ?: 0.0
            it[Keys.METERS] = current + amount
        }
    }

    suspend fun subtractMeters(amount: Double) {
        context.dataStore.edit {
            val current = it[Keys.METERS] ?: 0.0
            it[Keys.METERS] = (current - amount).coerceAtLeast(0.0)
        }
    }
}
