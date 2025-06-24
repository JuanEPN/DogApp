package com.sigmas.dogapp.Data.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "citas")
@Parcelize
data class Cita(
    @PrimaryKey val id: String = "", // ID ahora es String
    val nombrePropietario: String,
    val nombreMascota: String,
    val raza: String,
    val telefono: String,
    val sintomas: String? = null,
    val imagenUrl: String = "",
    val turno: Int = 0
) : Parcelable {
    // 🔧 Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "", null, "", 0)
}

