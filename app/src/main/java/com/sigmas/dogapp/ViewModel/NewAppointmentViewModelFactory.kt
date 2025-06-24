package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.ViewModel.NewAppointmentViewModel

class NewAppointmentViewModelFactory(
    private val repository: CitaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // ← elimina warning de cast inseguro
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewAppointmentViewModel::class.java)) {
            return NewAppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}