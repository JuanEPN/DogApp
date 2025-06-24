package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Repository.CitaRepository
import kotlinx.coroutines.launch

class CitaDetailViewModel(private val repository: CitaRepository) : ViewModel() {

    private val _cita = MutableLiveData<Cita?>()
    val cita: LiveData<Cita?> get() = _cita

    fun loadCitaById(id: Int) {
        viewModelScope.launch {
            val result = repository.getCitaById(id) // esta función es suspend
            _cita.postValue(result)
        }
    }

    fun deleteCita(cita: Cita) {
        viewModelScope.launch {
            repository.deleteCita(cita)
        }
    }
}
