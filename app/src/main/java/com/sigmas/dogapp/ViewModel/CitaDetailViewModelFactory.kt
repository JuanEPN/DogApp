package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.ViewModel.CitaDetailViewModel

class CitaDetailViewModelFactory(
    private val repository: CitaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitaDetailViewModel::class.java)) {
            return CitaDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}