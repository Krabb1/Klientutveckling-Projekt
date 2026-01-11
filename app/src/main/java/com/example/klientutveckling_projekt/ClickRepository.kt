package com.example.klientutveckling_projekt

import android.content.Context
import androidx.compose.ui.input.key.Key
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Kontrollerar persistent lagring
 *
 * Har read-only variabler som tillåter ett stadigt flöde av data till MainClicker fragmentet
 *
 * Tar emot en Context vilket ger tillgång till telefonens olika system, samt tillåter persistent lagring
 */
class ClickRepository(private val context: Context) {

    private object Keys {
        val METERS = doublePreferencesKey("meters")
        val METERS_PER_SECOND = doublePreferencesKey("meters_per_second")
        val LAST_ACTIVE = longPreferencesKey("last_active_timestamp")
        val PURCHASED_UPGRADES = stringSetPreferencesKey("purchased_upgrades")
    }

    val meters: Flow<Double> = context.dataStore.data
        .map { it[Keys.METERS] ?: 0.0 }

    val metersPerSeconds: Flow<Double> = context.dataStore.data
        .map { it[Keys.METERS_PER_SECOND] ?: 0.0 }

    val purchasedUpgrades: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[Keys.PURCHASED_UPGRADES]
            ?.map { it.toInt() }
            ?.toSet()
            ?:emptySet()
    }

    /**
     * Lägger till meter i persistent lagring
     *
     * @property amount Tar emot ett tal av typen Double
     */
    suspend fun addMeters(amount: Double) {
        context.dataStore.edit {
            val current = it[Keys.METERS] ?: 0.0
            it[Keys.METERS] = current + amount
        }
    }

    /**
     * Lägger till meter per sekund i persistent lagring
     *
     * @property amount emot ett tal av typen Double
     */
    suspend fun addMetersPerSecond(amount: Double){
        context.dataStore.edit {
            it[Keys.METERS_PER_SECOND] = (it[Keys.METERS_PER_SECOND] ?: 0.0) + amount
        }
    }

    /**
     * Lägger till köpta upgraderingar i persistent lagring
     *
     * De upgraderingar som lagras används senare för beräkning av multiplier
     *
     * @property id Tar emot en int som representerar en upgradering i allUpgrades
     */
    suspend fun addPurchasedUpgrade(id: Int){
        context.dataStore.edit {
            val current = it[Keys.PURCHASED_UPGRADES] ?: emptySet()
            it[Keys.PURCHASED_UPGRADES] = current + id.toString()
        }
    }

    /**
     * Subtraherar en specifierad mängd meter från totalen
     *
     * @property amount Tar emot ett tal av typen Double
     */
    suspend fun subtractMeters(amount: Double) {
        context.dataStore.edit {
            val current = it[Keys.METERS] ?: 0.0
            it[Keys.METERS] = (current - amount).coerceAtLeast(0.0)
        }
    }

    /**
     * Hämtar tiden sen användare var senast inloggad
     */
    suspend fun getLastActiveTime(): Long = context.dataStore.data
        .map { prefs -> prefs[Keys.LAST_ACTIVE] ?: 0L }
        .first()

    /**
     * Uppdaterar tiden sen användaren var senast online
     *
     * @property timeStamp Tar emot ett tal av typen Long som representerar tiden
     */
    suspend fun setLastActiveTime(timeStamp: Long){
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_ACTIVE] = timeStamp
        }
    }

    /**
     * Hämtar nuvarande värde för meter per sekund
     */
    suspend fun getMetersPerSecondOnce(): Double = context.dataStore.data
        .map { prefs -> prefs[Keys.METERS_PER_SECOND] ?: 0.0}.first()

    /**
     * Itererar över det persistenta lagren och tömmer dem
     */
    suspend fun reset(){
        context.dataStore.edit {
            it.clear()
        }
    }
}
