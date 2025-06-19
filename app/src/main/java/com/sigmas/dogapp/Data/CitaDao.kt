package com.sigmas.dogapp.Data

import androidx.room.*
import com.sigmas.dogapp.Data.Model.Cita
import androidx.lifecycle.LiveData


@Dao
interface CitaDao {

    @Query("SELECT * FROM citas")
    fun getAllAppointments(): LiveData<List<Cita>>

    @Insert
    suspend fun insertQuote(cita: Cita)

    @Update
    suspend fun updateQuote(cita: Cita)

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getAppointmentId(id: Int): Cita?

    @Delete
    suspend fun eliminar(cita: Cita)
}