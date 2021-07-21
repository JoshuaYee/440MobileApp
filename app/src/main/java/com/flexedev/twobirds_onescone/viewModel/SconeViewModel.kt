package com.flexedev.twobirds_onescone.viewModel

import androidx.lifecycle.*
import com.flexedev.twobirds_onescone.data.entities.Rating
import com.flexedev.twobirds_onescone.data.entities.Scone
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.flexedev.twobirds_onescone.data.SconeRepository
import kotlinx.coroutines.launch


class SconeViewModel(private val sconeRepository: SconeRepository) : ViewModel() {
    val scones: LiveData<List<SconeWithRatings>> = sconeRepository.scones.asLiveData()
    val numScones: LiveData<Int> = sconeRepository.numScones.asLiveData()

    fun addScone(scone: Scone) = viewModelScope.launch {
        sconeRepository.insert(scone)
    }

    fun addSconeWithRatings(scone: Scone, rating: Rating) = viewModelScope.launch {
        sconeRepository.insertSconeWithRatings(scone, rating)
    }

    fun updateScone(scone: Scone) = viewModelScope.launch {
        sconeRepository.updateScone(scone)
    }
    fun updateSconeAndRating(scone: Scone, rating: Rating) = viewModelScope.launch {
        sconeRepository.updateSconeAndRating(scone, rating)
    }


    fun deleteScone(scone: Scone) = viewModelScope.launch {
        sconeRepository.deleteScone(scone)
    }

    fun deleteSconeWithRating(sconeWR: SconeWithRatings) = viewModelScope.launch {
        sconeRepository.deleteSconeWithRatings(sconeWR)
    }

    fun getScone(id: Int) = viewModelScope.launch {
        sconeRepository.getOne(id)
    }
}

class SconeViewModelFactory(private val repository: SconeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SconeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SconeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
