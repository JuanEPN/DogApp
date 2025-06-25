package com.sigmas.dogapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Data.Model.RazasResponse
import com.sigmas.dogapp.Network.DogApiService
import com.sigmas.dogapp.Repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewAppointmentViewModel @Inject constructor(private val repository: CitaRepository, private val dogApiService: DogApiService) : ViewModel() {

    private val _razas = MutableLiveData<List<String>>()
    val razas: LiveData<List<String>> = _razas

    private val _citaGuardada = MutableLiveData<Boolean>()
    val citaGuardada: LiveData<Boolean> = _citaGuardada

    private val _imagenRaza = MutableLiveData<String>()
    val imagenRaza: LiveData<String> = _imagenRaza

    fun guardarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                repository.insertCita(cita)
                _citaGuardada.postValue(true)
            } catch (e: Exception) {
                _citaGuardada.postValue(false)
            }
        }
    }

    fun cargarRazasDesdeApi() {
        dogApiService.obtenerTodasLasRazas().enqueue(object : Callback<RazasResponse> {
            override fun onResponse(call: Call<RazasResponse>, response: Response<RazasResponse>) {
                if (response.isSuccessful) {
                    val razasMap = response.body()?.message ?: emptyMap()
                    val lista = razasMap.flatMap { (raza, subrazas) ->
                        if (subrazas.isEmpty()) listOf(raza)
                        else subrazas.map { "$raza $it" }
                    }.map { it.replaceFirstChar(Char::uppercaseChar) }.sorted()
                    _razas.postValue(lista)
                }
            }

            override fun onFailure(call: Call<RazasResponse>, t: Throwable) {
                _razas.postValue(emptyList())
            }
        })
    }


}