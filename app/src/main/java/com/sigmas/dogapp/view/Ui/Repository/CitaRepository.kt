package com.sigmas.dogapp.view.Ui.Repository

import androidx.lifecycle.LiveData
import com.sigmas.dogapp.view.Data.CitaDao
import com.sigmas.dogapp.view.Data.Model.Cita
import kotlinx.coroutines.flow.Flow

class CitaRepository(private val citaDao: CitaDao) {

    suspend fun insertar(cita: Cita) {
        citaDao.insertQuote(cita)
    }

    suspend fun actualizar(cita: Cita) {
        citaDao.updateQuote(cita)
    }


    suspend fun obtenerPorId(id: Int): Cita? {
        return citaDao.getAppointmentId(id)
    }

    suspend fun obtenerTodasLasCitas(): List<Cita> {
        return citaDao.getAllAppointments()
    }
}