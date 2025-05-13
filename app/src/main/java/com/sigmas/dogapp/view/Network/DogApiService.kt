package com.sigmas.dogapp.view.Network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import com.sigmas.dogapp.view.Data.Model.ImagenRazaResponse

interface DogApiService {
    @GET("breed/{raza}/images/random")
    fun obtenerImagenPorRaza(@Path("raza") raza: String): Call<ImagenRazaResponse>
}