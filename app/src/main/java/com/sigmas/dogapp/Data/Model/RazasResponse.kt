package com.sigmas.dogapp.Data.Model

// [Modelo de respuesta de razas de perros] (Representa la estructura de respuesta del API dog.ceo para obtener la lista de razas)
data class RazasResponse(
    val message: Map<String, List<String>>, // [Listado de razas y subrazas] (Clave: raza principal, Valor: lista de subrazas)
    val status: String                      // [Estado de la respuesta] ("success" o "error" según el resultado)
)
