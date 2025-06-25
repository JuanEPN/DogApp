package com.sigmas.dogapp.ui.CitaDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sigmas.dogapp.R
import com.sigmas.dogapp.databinding.FragmentCitaDetailBinding
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Data.Model.ImagenRazaResponse
import com.sigmas.dogapp.Network.DogApiService
import com.sigmas.dogapp.ui.Home.HomeActivity
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.network.RetrofitRazas
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class CitaDetailActivity : AppCompatActivity() {

    // [Variables principales] (Binding, repositorio de citas y la cita actual)
    private lateinit var binding: FragmentCitaDetailBinding
    private var citaActual: Cita? = null
    @Inject
    lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // [Desactivar modo oscuro] (Fuerza modo claro para evitar problemas de visibilidad)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        // [Inicializar binding] (Vincula el layout con el código usando ViewBinding)
        binding = FragmentCitaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Botón Editar] (Lanza la actividad para editar la cita y espera el resultado)
        binding.btnEditar.setOnClickListener {
            citaActual?.let { cita ->
                val intent = Intent(this, EditCitaActivity::class.java)
                intent.putExtra("cita", cita)
                editarCitaLauncher.launch(intent)
            }
        }

        // [Botón Borrar] (Muestra diálogo de confirmación para eliminar la cita)
        binding.btnBorrar.setOnClickListener {
            citaActual?.let { cita ->
                val show = AlertDialog.Builder(this)
                    .setTitle("¿Eliminar cita?")
                    .setMessage("¿Estás seguro de que deseas eliminar esta cita?")
                    .setPositiveButton("Sí") { _, _ ->
                        eliminarCita(cita)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        // [Obtener ID de cita] (Verifica si el ID es válido, si no, cierra la actividad)
        val idCita = intent.getStringExtra("id") ?: ""


        // [Botón Volver] (Regresa al Home de citas)
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // [Cargar datos de la cita] (Busca en la BD la cita por ID y actualiza la vista)
        cargarDatosCita(idCita)
    }

    // [Lanzador de resultado de edición] (Recarga los datos si se edita exitosamente)
    private val editarCitaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            citaActual?.id?.let { cargarDatosCita(it) }
        }
    }

    // [Cargar información de la cita] (Muestra los datos en pantalla y llama a la API de imagen)
    @SuppressLint("SetTextI18n")
    private fun cargarDatosCita(idCita: String) {
        lifecycleScope.launch {
            val cita = citaRepository.getCitaById(idCita)
            if (cita != null) {
                citaActual = cita
                binding.DetalleTurno.text = "#${cita.id}"
                binding.TituloNombreMascota.text = cita.nombreMascota
                binding.DetalleRaza.text = cita.raza
                binding.DetalleSintomas.text = "Síntomas: ${cita.sintomas ?: "No especificado"}"
                binding.DetallePropietario.text = "Propietario: ${cita.nombrePropietario}"
                binding.DetalleTelefono.text = "Teléfono: ${cita.telefono}"
                cargarImagenDesdeApi(normalizarRazaParaApi(cita.raza))
            } else {
                Toast.makeText(this@CitaDetailActivity, "Cita no encontrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // [Eliminar cita] (Borra la cita de la base de datos y regresa al Home)
    private fun eliminarCita(cita: Cita) {
        lifecycleScope.launch {
            try {
                citaRepository.deleteCita(cita)
                runOnUiThread {
                    Toast.makeText(this@CitaDetailActivity, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@CitaDetailActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@CitaDetailActivity, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // [Cargar imagen desde API] (Solicita una imagen de la raza y la muestra con Glide)
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

    // [Normalizar nombre de raza] (Convierte acentos y espacios para que coincida con la API)
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

    // [Mostrar imagen por defecto] (Carga una imagen local si falla la carga desde la API)
    private fun mostrarImagenPorDefecto() {
        Glide.with(this)
            .load(R.drawable.ic_dog)
            .into(binding.imgDetalleMascota)
    }
}