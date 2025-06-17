package com.sigmas.dogapp.view.Ui.Newappointment

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.databinding.ActivityNewAppointmentBinding
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Data.Model.Cita
import com.sigmas.dogapp.view.Data.Model.RazasResponse
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import com.sigmas.dogapp.view.Ui.Home.HomeActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class NewAppointmentActivity : AppCompatActivity() {

    // [Variables globales] (Binding y repositorio de citas)
    private lateinit var binding: ActivityNewAppointmentBinding
    private lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // [Modo claro forzado] (Evita interferencias visuales con dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        // [Inicializar Binding y setContentView] (Inflamos la vista de forma segura)
        binding = ActivityNewAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Instancia de base de datos y repositorio] (Acceso a Room para insertar citas)
        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        // [Configuraciones iniciales de la pantalla] (Dropdowns, API razas, insets y validaciones)
        configurarDropdown()
        cargarRazasDesdeApi()
        configurarInsets()
        configurarValidacion()

        // [Botón guardar cita] (Validación de síntoma y almacenamiento)
        binding.btnGuardar.setOnClickListener {
            if (binding.spinnerSintomas.text.toString() == "Síntomas") {
                Toast.makeText(this, "Selecciona un síntoma", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            guardarCita()
        }

        // [Botón volver] (Regresa a pantalla Home)
        binding.btnBack.setOnClickListener {
            navegarAHome()
        }
    }

    // [Navegación a HomeActivity] (Cuando el usuario presiona volver)
    private fun navegarAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // [Configurar dropdown de síntomas] (Lista de síntomas y comportamiento del spinner)
    private fun configurarDropdown() {
        val sintomasList = listOf(
            "Síntomas",  // valor inicial por defecto
            "Solo duerme",
            "No come",
            "Fractura extremidad",
            "Tiene pulgas",
            "Tiene garrapatas",
            "Bota demasiado pelo"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sintomasList)
        binding.spinnerSintomas.setAdapter(adapter)
        binding.spinnerSintomas.setText("Síntomas", false)
        binding.spinnerSintomas.setOnClickListener {
            binding.spinnerSintomas.showDropDown()
        }
    }

    // [Aplicar márgenes seguros] (Insets para evitar solapamiento con barras del sistema)
    private fun configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // [Validación de campos del formulario] (Escucha cambios y activa botón guardar si es válido)
    private fun configurarValidacion() {
        val campos = listOf(
            binding.etNombreMascota,
            binding.etRaza,
            binding.etNombrePropietario,
            binding.etTelefono,
            binding.spinnerSintomas
        )

        campos.forEach { campo ->
            campo.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    validarCamposObligatorios()
                }
            })
        }
    }

    // [Guardar cita en Room] (Toma los datos del formulario y los almacena localmente)
    private fun guardarCita() {
        val cita = Cita(
            nombrePropietario = binding.etNombrePropietario.text.toString().trim(),
            nombreMascota = binding.etNombreMascota.text.toString().trim(),
            raza = binding.etRaza.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim(),
            sintomas = binding.spinnerSintomas.text.toString().trim(),
            imagenUrl = "",
            turno = 0
        )

        lifecycleScope.launch {
            try {
                citaRepository.insertar(cita)
                runOnUiThread {
                    Toast.makeText(this@NewAppointmentActivity, "Cita Guardada", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    val intent = Intent(this@NewAppointmentActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@NewAppointmentActivity, "Error al guardar cita: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // [Limpiar campos del formulario] (Después de guardar una cita exitosamente)
    private fun limpiarCampos() {
        binding.etNombrePropietario.text?.clear()
        binding.etNombreMascota.text?.clear()
        binding.etRaza.text?.clear()
        binding.etTelefono.text?.clear()
        binding.spinnerSintomas.text?.clear()
    }

    // [Validar que todos los campos estén llenos] (Activa o desactiva el botón Guardar)
    private fun validarCamposObligatorios() {
        val nombreMascota = binding.etNombreMascota.text?.isNotEmpty() == true
        val raza = binding.etRaza.text?.isNotEmpty() == true
        val nombrePropietario = binding.etNombrePropietario.text?.isNotEmpty() == true
        val telefono = binding.etTelefono.text?.isNotEmpty() == true
        val sintomas = binding.spinnerSintomas.text?.isNotEmpty() == true

        val camposValidos = nombreMascota && raza && nombrePropietario && telefono
        binding.btnGuardar.isEnabled = camposValidos

        // [Color del texto del botón según validación] (Visualmente indica si está activo)
        binding.btnGuardar.setTextColor(
            if (camposValidos) resources.getColor(android.R.color.white)
            else resources.getColor(android.R.color.darker_gray)
        )
    }

    // [Cargar razas desde la API] (Usa Retrofit para obtener lista de razas del endpoint)
    private fun cargarRazasDesdeApi() {
        val apiService = com.sigmas.dogapp.view.Network.RetrofitRazas
            .instance.create(com.sigmas.dogapp.view.Network.DogApiService::class.java)

        apiService.obtenerTodasLasRazas().enqueue(object : retrofit2.Callback<com.sigmas.dogapp.view.Data.Model.RazasResponse> {
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
                        this@NewAppointmentActivity,
                        R.layout.simple_dropdown_item_1line,
                        listaRazas
                    )

                    binding.etRaza.setAdapter(adapter)
                    binding.etRaza.setOnClickListener {
                        binding.etRaza.showDropDown()
                    }
                } else {
                    Toast.makeText(this@NewAppointmentActivity, "Error al cargar razas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<com.sigmas.dogapp.view.Data.Model.RazasResponse>,
                t: Throwable
            ) {
                Toast.makeText(this@NewAppointmentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
