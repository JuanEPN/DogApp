package com.sigmas.dogapp.Repository

import androidx.lifecycle.LiveData
import com.sigmas.dogapp.Data.CitaDao
import com.sigmas.dogapp.Data.Model.Cita

class CitaRepository(private val citaDao: CitaDao) {

    fun getAllCitas(): LiveData<List<Cita>> {
        return citaDao.getAllAppointments()
    }

    suspend fun insertCita(cita: Cita) {
        citaDao.insertQuote(cita)
    }

    suspend fun updateCita(cita: Cita) {
        citaDao.updateQuote(cita)
    }

    suspend fun getCitaById(id: Int): Cita? {
        return citaDao.getAppointmentId(id)
    }

    suspend fun deleteCita(cita: Cita) {
        citaDao.eliminar(cita)
    }
}