package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitaDetailViewModel @Inject constructor(private val repository: CitaRepository) : ViewModel() {

    private val _cita = MutableLiveData<Cita?>()
    val cita: LiveData<Cita?> get() = _cita

    fun loadCitaById(id: String) {
        viewModelScope.launch {
            val result = repository.getCitaById(id)
            _cita.postValue(result)
        }
    }

    fun deleteCita(cita: Cita) {
        viewModelScope.launch {
            repository.deleteCita(cita)
        }
    }
}
