package com.sigmas.dogapp.view.Ui.CitaDetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.databinding.ActivityEditAppointmentBinding
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Data.Model.Cita
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import kotlinx.coroutines.launch

class EditCitaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAppointmentBinding
    private lateinit var citaRepository: CitaRepository
    private var citaActual: Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        citaActual = intent.getParcelableExtra("cita")

        if (citaActual != null) {
            cargarDatosEnCampos(citaActual!!)
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
                    Toast.makeText(this@EditCitaActivity, "Cita actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
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