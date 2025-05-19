package com.sigmas.dogapp.view.Ui.CitaDetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sigmas.dogapp.R
import com.sigmas.dogapp.databinding.ActivityCitaDetailBinding
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Data.Model.Cita
import com.sigmas.dogapp.view.Data.Model.ImagenRazaResponse
import com.sigmas.dogapp.view.Network.DogApiService
import com.sigmas.dogapp.view.Network.RetrofitRazas
import com.sigmas.dogapp.view.Ui.Home.HomeActivity
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitaDetailBinding
    private lateinit var citaRepository: CitaRepository
    private var citaActual: Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityCitaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnEditar.setOnClickListener {
            citaActual?.let { cita ->
                val intent = Intent(this, EditCitaActivity::class.java)
                intent.putExtra("cita", cita) // envías toda la cita
                startActivity(intent)
            }
        }

        val idCita = intent.getIntExtra("id", -1)
        if (idCita == -1) {
            Toast.makeText(this, "Error: ID de cita inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        citaRepository = CitaRepository(AppDatabase.getDatabase(applicationContext).citaDao())

        binding.btnVolver.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        cargarDatosCita(idCita)
    }

    private fun cargarDatosCita(idCita: Int) {
        lifecycleScope.launch {
            val cita = citaRepository.obtenerPorId(idCita)
            if (cita != null) {
                citaActual = cita
                binding.tvDetalleTurno.text = "#${cita.id}"
                binding.tvDetalleNombreMascota.text = cita.nombreMascota
                binding.tvDetalleSintomas.text = "Síntomas: ${cita.sintomas ?: "No especificado"}"
                binding.tvDetallePropietario.text = "Propietario: ${cita.nombrePropietario}"
                binding.tvDetalleTelefono.text = "Teléfono: ${cita.telefono}"
                cargarImagenDesdeApi(normalizarRazaParaApi(cita.raza))
            } else {
                Toast.makeText(this@CitaDetailActivity, "Cita no encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun cargarImagenDesdeApi(razaApi: String) {
        val apiService = RetrofitRazas.instance.create(DogApiService::class.java)
        apiService.obtenerImagenPorRaza(razaApi).enqueue(object : Callback<ImagenRazaResponse> {
            override fun onResponse(call: Call<ImagenRazaResponse>, response: Response<ImagenRazaResponse>) {
                if (response.isSuccessful) {
                    val imagenUrl = response.body()?.message
                    if (!imagenUrl.isNullOrEmpty()) {
                        Glide.with(this@CitaDetailActivity)
                            .load(imagenUrl)
                            .placeholder(R.drawable.ic_dog)
                            .error(R.drawable.ic_dog)
                            .into(binding.imgDetalleMascota)
                    } else {
                        mostrarImagenPorDefecto()
                    }
                } else {
                    Log.e("API_RESPONSE", "Error: ${response.code()} ${response.message()}")
                    mostrarImagenPorDefecto()
                }
            }

            override fun onFailure(call: Call<ImagenRazaResponse>, t: Throwable) {
                Log.e("API_CALL", "Fallo en la API: ${t.message}", t)
                mostrarImagenPorDefecto()
            }
        })
    }

    private fun normalizarRazaParaApi(raza: String): String {
        return raza.lowercase()
            .replace("[áàäâ]".toRegex(), "a")
            .replace("[éèëê]".toRegex(), "e")
            .replace("[íìïî]".toRegex(), "i")
            .replace("[óòöô]".toRegex(), "o")
            .replace("[úùüû]".toRegex(), "u")
            .replace("[^a-z/]".toRegex(), "")
            .replace(" ", "")
    }

    private fun mostrarImagenPorDefecto() {
        Glide.with(this)
            .load(R.drawable.ic_dog)
            .into(binding.imgDetalleMascota)
    }
}


