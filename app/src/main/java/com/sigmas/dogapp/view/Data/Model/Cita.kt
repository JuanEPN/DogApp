package com.sigmas.dogapp.view.Data.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// [Entidad Cita para Room + Parcelable] (Modelo de datos que representa una cita en la base de datos local y puede enviarse entre Activities)
@Entity(tableName = "citas")
@Parcelize
data class Cita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,         // [ID autogenerado] (Clave primaria única para cada cita)
    val nombrePropietario: String,                            // [Nombre del propietario] (Texto del dueño de la mascota)
    val nombreMascota: String,                                // [Nombre de la mascota] (Nombre del perro o animal)
    val raza: String,                                         // [Raza de la mascota] (Nombre de la raza o subraza)
    val telefono: String,                                     // [Teléfono del propietario] (Número de contacto)
    val sintomas: String? = null,                             // [Síntomas reportados] (Motivo de la cita, puede ser nulo)
    val imagenUrl: String = "",                               // [URL de imagen] (URL de imagen de la raza obtenida de la API)
    val turno: Int = 0                                        // [Número de turno] (Valor numérico opcional para asignar orden)
) : Parcelable // [Intercambiable entre Activities] (Permite enviar esta clase entre pantallas usando Intents)
