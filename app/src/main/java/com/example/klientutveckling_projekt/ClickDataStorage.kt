package com.example.klientutveckling_projekt

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "click_data")

object ClickKeys {
    val CLICKS = intPreferencesKey("clicks")
}
