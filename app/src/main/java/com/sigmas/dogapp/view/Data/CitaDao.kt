package com.sigmas.dogapp.view.Data

import androidx.room.*
import com.sigmas.dogapp.view.Data.Model.Cita

@Dao
interface CitaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(cita: Cita)

    @Update
    suspend fun updateQuote(cita: Cita)

    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getAppointmentId(id: Int): Cita?

    @Query("SELECT * FROM citas")
    suspend fun getAllAppointments(): List<Cita>

    @Delete
    suspend fun eliminar(cita: Cita)
}

