package com.example.klientutveckling_projekt

import com.example.klientutveckling_projekt.Upgrade
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedViewModel(
    private val repository: ClickRepository
) : ViewModel() {



    public val allUpgrades = listOf(
        Upgrade(1, "Faster Clicks", "More meters per click", 1.1, 10.0),
        Upgrade(2, "Stronger Drill", "Dig deeper", 1.25, 25.0),
        Upgrade(3, "Turbo Mode", "Big boost", 1.5, 50.0),
        Upgrade(4, "Mega Drill", "Huge boost", 2.0, 100.0)
    )
    private val _purchasedUpgrades = MutableStateFlow<Set<Int>>(emptySet())
    val purchasedUpgrades: StateFlow<Set<Int>> = _purchasedUpgrades.asStateFlow()

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
    private val _multiplier = MutableStateFlow(1.0)

    val meters: StateFlow<Double> = repository.meters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    fun click() {
        viewModelScope.launch {
            repository.addMeters(multiplier.value)
        }
    }

    fun buyUpgrade(upgrade: Upgrade): Boolean {
        if (meters.value < upgrade.cost) return false
        if (upgrade.id in _purchasedUpgrades.value) return false

        viewModelScope.launch {
            repository.subtractMeters(upgrade.cost)
        }

        _purchasedUpgrades.value += upgrade.id

        return true
    }
}
