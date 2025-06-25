package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CitaRepository) : ViewModel() {

    val citas: LiveData<List<Cita>> = repository.getAllCitas()

    fun eliminarCita(cita: Cita) {
        viewModelScope.launch {
            repository.deleteCita(cita)
        }
    }

    fun insertarCita(cita: Cita) {
        viewModelScope.launch {
            repository.insertCita(cita)
        }
    }
}


