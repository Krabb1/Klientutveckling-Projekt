package com.example.klientutveckling_projekt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModelFactory som skapar SharedViewModels för fragmenten
 *
 * Skapar SharedViewModel och injecerar en repository
 *
 * @property repository Tar emot en typ av ClickRepository
 *
 * @return Returnerar en ViewModelProvider.Factory så att activityViewModels() kan använda den
 */
class ViewModelFactory(private val repository: ClickRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SharedViewModel::class.java)){
            return SharedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unkown Viewmodel")
    }

}