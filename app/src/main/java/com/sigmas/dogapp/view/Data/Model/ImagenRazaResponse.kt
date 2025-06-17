package com.sigmas.dogapp.view.Data.Model

// [Modelo de respuesta de imagen de raza] (Representa la respuesta JSON del API dog.ceo para obtener una imagen de una raza)
data class ImagenRazaResponse(
    val message: String,  // [URL de la imagen] (Contiene la URL de la imagen de la raza)
    val status: String    // [Estado de la respuesta] ("success" o "error" según el resultado)
)
