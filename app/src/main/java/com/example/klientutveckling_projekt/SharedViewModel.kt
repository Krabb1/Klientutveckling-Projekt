package com.example.klientutveckling_projekt

import androidx.compose.ui.unit.min
import com.example.klientutveckling_projekt.Upgrade
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first


class SharedViewModel(
    private val repository: ClickRepository
) : ViewModel() {

private companion object{
    private const val MAX_OFFLINEPROGRESS: Long = 8*60*60
}

    public val allUpgrades = listOf(
        Upgrade(1, "Faster Clicks", "More meters per click", 1.1, 10.0),
        Upgrade(2, "Stronger Drill", "Dig deeper", 1.25, 25.0),
        Upgrade(3, "Turbo Mode", "Big boost", 1.5, 50.0),
        Upgrade(4, "Mega Drill", "Huge boost", 2.0, 100.0),
        Upgrade(5, "AutoDriller", "Passive drilling", 1.0, 10.0, 1.0)
    )


    private val _multiplier = MutableStateFlow(0)

    val purchasedUpgrades: StateFlow<Set<Int>> = repository.purchasedUpgrades
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val multiplier: StateFlow<Double> = purchasedUpgrades
        .map{purchasedIds ->
            allUpgrades.filter {it.id in purchasedIds}.fold(1.0){acc, upgrade ->
                acc *upgrade.multiplier
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 1.0
        )

    val meters: StateFlow<Double> = repository.meters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    val metersPerSecond: StateFlow<Double> = repository.metersPerSeconds.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0.0
    )



    init {
        applyOfflineProgress()
        startPassiveIncome()
    }

    private fun startPassiveIncome() {
        viewModelScope.launch {
            while (true){
                delay(1000)
                val mps = metersPerSecond.value
                if (mps > 0) {
                    repository.addMeters(mps)
                }
            }
        }
    }

    private fun applyOfflineProgress(){
        viewModelScope.launch {
            try {
                val lastActive = repository.getLastActiveTime()
                val now = System.currentTimeMillis()
                if (lastActive <= 0L){
                    repository.setLastActiveTime(now)
                    return@launch
                }

                val rawSecondsOffline = (now - lastActive) / 1000.0
                if (rawSecondsOffline <= 0.0){
                    repository.setLastActiveTime(now)
                    return@launch
                }

                val secondsOffline = Math.min(rawSecondsOffline, MAX_OFFLINEPROGRESS.toDouble())

                val mps = repository.getMetersPerSecondOnce()

                val earned = secondsOffline * mps

                if (earned > 0.0){
                    repository.addMeters(earned)
                }

                repository.setLastActiveTime(now)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }



    fun click() {
        viewModelScope.launch {
            repository.addMeters(multiplier.value)
        }
    }

    fun addPassiveUpgrade(amount: Double){
        viewModelScope.launch {
            repository.addMetersPerSecond(amount)
        }
    }

    fun buyUpgrade(upgrade: Upgrade): Boolean {
        if (meters.value < upgrade.cost) return false
        if (upgrade.id in purchasedUpgrades.value) return false

        viewModelScope.launch {
            repository.subtractMeters(upgrade.cost)
            repository.addPurchasedUpgrade(upgrade.id)

            if (upgrade.metersPerSecondBonus > 0) {
                repository.addMetersPerSecond(upgrade.metersPerSecondBonus)
            }
        }


        return true
    }


    suspend fun reset() {


        repository.reset()

    }
}
