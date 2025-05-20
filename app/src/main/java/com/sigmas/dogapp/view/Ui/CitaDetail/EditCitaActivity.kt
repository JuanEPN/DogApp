package com.sigmas.dogapp.view.Ui.CitaDetail

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.databinding.ActivityEditAppointmentBinding
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Data.Model.Cita
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class EditCitaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAppointmentBinding
    private lateinit var citaRepository: CitaRepository
    private var citaActual: Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        citaActual = intent.getParcelableExtra("cita")

        if (citaActual != null) {
            cargarDatosEnCampos(citaActual!!)
            cargarRazasDesdeApi()
        } else {
            Toast.makeText(this, "No se encontró la cita", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnGuardar.setOnClickListener {
            guardarCambios()
        }

        configurarValidacionCampos()
    }

    private fun cargarDatosEnCampos(cita: Cita) {
        binding.etNombreMascota.setText(cita.nombreMascota)
        binding.etRaza.setText(cita.raza)
        binding.etNombrePropietario.setText(cita.nombrePropietario)
        binding.etTelefono.setText(cita.telefono)
    }

    private fun guardarCambios() {
        val citaEditada = citaActual?.copy(
            nombreMascota = binding.etNombreMascota.text.toString().trim(),
            raza = binding.etRaza.text.toString().trim(),
            nombrePropietario = binding.etNombrePropietario.text.toString().trim(),
            telefono = binding.etTelefono.text.toString().trim()
        )

        if (citaEditada != null) {
            lifecycleScope.launch {
                citaRepository.actualizar(citaEditada)
                runOnUiThread {
                    Toast.makeText(this@EditCitaActivity, "Cita Actualizada", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun cargarRazasDesdeApi() {
        val apiService = com.sigmas.dogapp.view.Network.RetrofitRazas
            .instance.create(com.sigmas.dogapp.view.Network.DogApiService::class.java)

        apiService.obtenerTodasLasRazas().enqueue(object : retrofit2.Callback<com.sigmas.dogapp.view.Data.Model.RazasResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.sigmas.dogapp.view.Data.Model.RazasResponse>,
                response: retrofit2.Response<com.sigmas.dogapp.view.Data.Model.RazasResponse>
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
                call: retrofit2.Call<com.sigmas.dogapp.view.Data.Model.RazasResponse>,
                t: Throwable
            ) {
                Toast.makeText(this@EditCitaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

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