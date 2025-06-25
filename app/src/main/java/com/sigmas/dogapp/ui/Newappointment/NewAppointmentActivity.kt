package com.sigmas.dogapp.ui.Newappointment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sigmas.dogapp.R
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Data.Model.RazasResponse
import com.sigmas.dogapp.databinding.ActivityNewAppointmentBinding
import com.sigmas.dogapp.Network.DogApiService
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.network.RetrofitRazas
import com.sigmas.dogapp.ui.Home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class NewAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAppointmentBinding
    @Inject
    lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityNewAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        configurarDropdown()
        cargarRazasDesdeApi()
        configurarInsets()
        configurarValidacion()

        binding.btnGuardar.setOnClickListener {
            if (binding.spinnerSintomas.text.toString() == getString(R.string.sintomas)) {
                Toast.makeText(this, getString(R.string.selecciona_sintoma), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            guardarCita()
        }

        binding.btnBack.setOnClickListener {
            navegarAHome()
        }
    }


    private fun navegarAHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun configurarDropdown() {
        val sintomasList = listOf(
            getString(R.string.sintomas),
            "Solo duerme",
            "No come",
            "Fractura extremidad",
            "Tiene pulgas",
            "Tiene garrapatas",
            "Bota demasiado pelo"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sintomasList)
        binding.spinnerSintomas.setAdapter(adapter)
        binding.spinnerSintomas.setText(getString(R.string.sintomas), false)
        binding.spinnerSintomas.setOnClickListener {
            binding.spinnerSintomas.showDropDown()
        }
    }

    private fun configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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
            campo.addTextChangedListener(object : TextWatcher {
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
                citaRepository.insertCita(cita)
                runOnUiThread {
                    Toast.makeText(this@NewAppointmentActivity, getString(R.string.cita_guardada), Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    startActivity(Intent(this@NewAppointmentActivity, HomeActivity::class.java))
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
        val camposValidos = listOf(
            binding.etNombreMascota.text,
            binding.etRaza.text,
            binding.etNombrePropietario.text,
            binding.etTelefono.text
        ).all { it?.isNotEmpty() == true }

        binding.btnGuardar.isEnabled = camposValidos
        binding.btnGuardar.setTextColor(
            ContextCompat.getColor(
                this,
                if (camposValidos) R.color.white else android.R.color.white
            )
        )
    }

    private fun cargarRazasDesdeApi() {
        val apiService = RetrofitRazas.instance.create(DogApiService::class.java)
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
                        this@NewAppointmentActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        listaRazas
                    )

                    binding.etRaza.setAdapter(adapter)
                    binding.etRaza.setOnClickListener {
                        binding.etRaza.showDropDown()
                    }
                } else {
                    Toast.makeText(this@NewAppointmentActivity, getString(R.string.error_cargar_razas), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RazasResponse>, t: Throwable) {
                Toast.makeText(this@NewAppointmentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}