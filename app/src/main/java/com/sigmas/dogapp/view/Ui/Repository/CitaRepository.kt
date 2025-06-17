package com.sigmas.dogapp.view.Ui.Repository

import com.sigmas.dogapp.view.Data.CitaDao
import com.sigmas.dogapp.view.Data.Model.Cita

// [Repositorio de citas] (Encapsula el acceso a la base de datos usando el DAO)
class CitaRepository(private val citaDao: CitaDao) {

    // [Insertar cita] (Agrega una nueva cita a la base de datos)
    suspend fun insertar(cita: Cita) {
        citaDao.insertQuote(cita)
    }

    // [Actualizar cita] (Modifica una cita existente en la base de datos)
    suspend fun actualizar(cita: Cita) {
        citaDao.updateQuote(cita)
    }

    // [Obtener cita por ID] (Devuelve una cita específica según su ID)
    suspend fun obtenerPorId(id: Int): Cita? {
        return citaDao.getAppointmentId(id)
    }

    // [Obtener todas las citas] (Devuelve la lista completa de citas almacenadas)
    suspend fun obtenerTodasLasCitas(): List<Cita> {
        return citaDao.getAllAppointments()
    }

    // [Eliminar cita] (Elimina una cita específica de la base de datos)
    suspend fun eliminar(cita: Cita) {
        citaDao.eliminar(cita)
    }
}
