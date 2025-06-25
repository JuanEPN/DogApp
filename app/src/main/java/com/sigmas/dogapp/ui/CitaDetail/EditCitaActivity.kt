package com.sigmas.dogapp.ui.CitaDetail

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.databinding.FragmentEditarCitaBinding
import com.sigmas.dogapp.Data.AppDatabase
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Data.Model.RazasResponse
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
@Suppress("DEPRECATION")
class EditCitaActivity : AppCompatActivity() {

    // [Variables principales] (Binding, repositorio y cita actual a editar)
    private lateinit var binding: FragmentEditarCitaBinding
    private var citaActual: Cita? = null
    @Inject
    lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // [Forzar modo claro] (Desactiva modo oscuro para mantener diseño consistente)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        // [Inicializar binding y vista] (Carga el layout con ViewBinding)
        binding = FragmentEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Inicializar base de datos y repositorio] (Se conecta con Room vía DAO)
        val database = AppDatabase.getDatabase(applicationContext)

        // [Obtener cita del intent] (Recoge la cita enviada desde otra actividad)
        citaActual = intent.getParcelableExtra("cita")

        // [Verificar si existe cita] (Si no, mostrar mensaje y cerrar actividad)
        if (citaActual != null) {
            cargarDatosEnCampos(citaActual!!)
            cargarRazasDesdeApi()
        } else {
            Toast.makeText(this, "No se encontró la cita", Toast.LENGTH_SHORT).show()
            finish()
        }

        // [Botón atrás] (Cierra esta ventana y regresa)
        binding.btnBack.setOnClickListener {
            finish()
        }

        // [Botón guardar] (Llama a la función que actualiza los cambios)
        binding.btnGuardar.setOnClickListener {
            guardarCambios()
        }

        // [Validación de campos] (Activa o desactiva el botón según si los campos están llenos)
        configurarValidacionCampos()
    }

    // [Cargar datos en los campos] (Llena los EditText con los datos de la cita actual)
    private fun cargarDatosEnCampos(cita: Cita) {
        binding.etNombreMascota.setText(cita.nombreMascota)
        binding.etRaza.setText(cita.raza)
        binding.etNombrePropietario.setText(cita.nombrePropietario)
        binding.etTelefono.setText(cita.telefono)
    }

    // [Guardar cambios] (Actualiza la cita en la base de datos con los nuevos datos y vuelve a Home)
    private fun guardarCambios() {
        val citaEditada = citaActual?.copy(
            nombreMascota = binding.etNombreMascota.text.toString().trim(),
            raza = binding.etRaza.text.toString().trim(),
            nombrePropietario = binding.etNombrePropietario.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim()
        )

        if (citaEditada != null) {
            lifecycleScope.launch {
                citaRepository.updateCita(citaEditada)
                runOnUiThread {
                    Toast.makeText(this@EditCitaActivity, "Cita Actualizada", Toast.LENGTH_SHORT).show()

                    // [Redirigir a HU 2.0 Home] (Cierra todas las pantallas anteriores)
                    val intent = Intent(this@EditCitaActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    // [Cargar razas desde API] (Obtiene las razas de perros usando Retrofit y las muestra como sugerencias)
    private fun cargarRazasDesdeApi() {
        val apiService = RetrofitRazas
            .instance.create(DogApiService::class.java)

        apiService.obtenerTodasLasRazas().enqueue(object : Callback<RazasResponse> {
            override fun onResponse(
                call: Call<RazasResponse>,
                response: Response<RazasResponse>
            ) {
                if (response.isSuccessful) {
                    val razasMap = response.body()?.message ?: emptyMap()

                    val listaRazas = razasMap.flatMap { (raza, subrazas) ->
                        if (subrazas.isEmpty()) listOf(raza)
                        else subrazas.map { sub -> "$raza $sub" }
                    }.map { it.replaceFirstChar(Char::uppercaseChar) }.sorted()

                    val adapter = ArrayAdapter(
                        this@EditCitaActivity,
                        R.layout.simple_dropdown_item_1line,
                        listaRazas
                    )
                    binding.etRaza.setAdapter(adapter)
                    binding.etRaza.setOnClickListener {
                        binding.etRaza.showDropDown()
                    }
                } else {
                    Toast.makeText(this@EditCitaActivity, "Error al cargar razas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RazasResponse>,
                t: Throwable
            ) {
                Toast.makeText(this@EditCitaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // [Validación de campos de texto] (Habilita el botón si todos los campos están llenos)
    private fun configurarValidacionCampos() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val camposLlenos = binding.etNombreMascota.text!!.isNotEmpty() &&
                        binding.etRaza.text!!.isNotEmpty() &&
                        binding.etNombrePropietario.text!!.isNotEmpty() &&
                        binding.etTelefono.text!!.isNotEmpty()
                binding.btnGuardar.isEnabled = camposLlenos
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etNombreMascota.addTextChangedListener(watcher)
        binding.etRaza.addTextChangedListener(watcher)
        binding.etNombrePropietario.addTextChangedListener(watcher)
        binding.etTelefono.addTextChangedListener(watcher)
    }
}
