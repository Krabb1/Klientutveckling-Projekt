package com.example.klientutveckling_projekt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//Detta är bara en experimentell klass med viewmodel, vet ej om det kommer behövas men la in den utifall.
//Metod/metoder kommer behöva justeras efter behov
class SharedViewModel(private val repository: ClickRepository) : ViewModel() {

    val clicks: StateFlow<Int> = repository.clicks
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )



    fun clicksIncrease(){
        viewModelScope.launch {
            repository.incrementClicks()
        }
    }
}