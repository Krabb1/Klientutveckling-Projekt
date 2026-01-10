package com.example.klientutveckling_projekt

import com.example.klientutveckling_projekt.Upgrade
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedViewModel(
    private val repository: ClickRepository
) : ViewModel() {

    private val _multiplier = MutableStateFlow(1.0)
    val multiplier: StateFlow<Double> = _multiplier.asStateFlow()

    private val _purchasedUpgrades = MutableStateFlow<Set<Int>>(emptySet())
    val purchasedUpgrades: StateFlow<Set<Int>> = _purchasedUpgrades.asStateFlow()

    val meters: StateFlow<Double> = repository.meters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )

    fun click() {
        viewModelScope.launch {
            repository.addMeters(_multiplier.value)
        }
    }

    fun buyUpgrade(upgrade: Upgrade): Boolean {
        if (meters.value < upgrade.cost) return false
        if (upgrade.id in _purchasedUpgrades.value) return false

        viewModelScope.launch {
            repository.subtractMeters(upgrade.cost)
        }

        _multiplier.value *= upgrade.multiplier
        _purchasedUpgrades.value += upgrade.id

        return true
    }
}
