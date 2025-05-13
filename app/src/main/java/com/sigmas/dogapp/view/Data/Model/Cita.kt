package com.sigmas.dogapp.view.Data.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "citas")
@Parcelize
data class Cita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombrePropietario: String,
    val nombreMascota: String,
    val raza: String,
    val telefono: String,
    val sintomas: String ? = null,
    val imagenUrl: String = "",
    val turno: Int = 0
) : Parcelable
