package com.sigmas.dogapp.view.Network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import com.sigmas.dogapp.view.Data.Model.ImagenRazaResponse
import com.sigmas.dogapp.view.Data.Model.RazasResponse

interface DogApiService {
    @GET("breed/{raza}/images/random")
    fun obtenerImagenPorRaza(@Path("raza") raza: String): Call<ImagenRazaResponse>
    @GET("breeds/list/all")
    fun obtenerTodasLasRazas(): Call<RazasResponse>
}