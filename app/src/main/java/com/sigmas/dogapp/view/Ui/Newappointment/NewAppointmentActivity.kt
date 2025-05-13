package com.sigmas.dogapp.view.Ui.Newappointment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.R
import com.sigmas.dogapp.databinding.ActivityNewAppointmentBinding
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Data.Model.Cita
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import com.sigmas.dogapp.view.Ui.Home.HomeActivity
import kotlinx.coroutines.launch

class NewAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAppointmentBinding
    private lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar repositorio
        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        configurarDropdown()
        configurarInsets()
        configurarValidacion()

        binding.btnGuardar.setOnClickListener {
            guardarCita()
        }

        binding.btnBack.setOnClickListener {
            navegarAHome()
        }
    }

    private fun navegarAHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun configurarDropdown() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.SymptomList,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.spinnerSintomas.setAdapter(adapter)
        binding.spinnerSintomas.setOnClickListener {
            binding.spinnerSintomas.showDropDown()
        }
    }

    private fun configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

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
                    Toast.makeText(this@NewAppointmentActivity, "Cita guardada", Toast.LENGTH_SHORT).show()
                    limpiarCampos()

                    // Navegar a Home
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

    private fun limpiarCampos() {
        binding.etNombrePropietario.text?.clear()
        binding.etNombreMascota.text?.clear()
        binding.etRaza.text?.clear()
        binding.etTelefono.text?.clear()
        binding.spinnerSintomas.text?.clear()
    }

    private fun validarCamposObligatorios() {
        val camposCompletos = binding.etNombreMascota.text?.isNotEmpty() == true &&
                binding.etRaza.text?.isNotEmpty() == true &&
                binding.etNombrePropietario.text?.isNotEmpty() == true &&
                binding.etTelefono.text?.isNotEmpty() == true &&
                binding.spinnerSintomas.text?.isNotEmpty() == true

        binding.btnGuardar.isEnabled = camposCompletos
    }
}