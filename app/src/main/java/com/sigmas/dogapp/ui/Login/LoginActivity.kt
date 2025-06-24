package com.sigmas.dogapp.Ui.Login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.sigmas.dogapp.databinding.ActivityLoginBinding
import com.sigmas.dogapp.ui.Home.HomeActivity

class LoginActivity : AppCompatActivity() {

    // [Variables principales] (Binding y Firebase Auth)
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // [Deshabilitar botones inicialmente]
        binding.btnLogin.isEnabled = false
        binding.tvRegister.isEnabled = false
        actualizarColorBotones(false)

        // [Activar botones solo si los campos están llenos]
        binding.etEmail.addTextChangedListener { validarBotones() }
        binding.etPassword.addTextChangedListener { validarBotones() }

        // [Login existente]
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (!validarCampos(email, password)) return@setOnClickListener

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navegarAHU2()
                    } else {
                        Toast.makeText(this, "Login Incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // [Registrar nuevo usuario]
        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (!validarCampos(email, password)) return@setOnClickListener

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login registrado correctamente", Toast.LENGTH_SHORT).show()
                        navegarAHU2()
                    } else {
                        Toast.makeText(this, "Error: este usuario ya existe o es inválido", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // [Validar email y password] (Formato, longitud mínima y campos llenos)
    private fun validarCampos(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // [Activar botones si los campos están llenos]
    private fun validarBotones() {
        val emailLleno = binding.etEmail.text?.isNotEmpty() == true
        val passwordLleno = binding.etPassword.text?.isNotEmpty() == true
        val habilitar = emailLleno && passwordLleno

        binding.btnLogin.isEnabled = habilitar
        binding.tvRegister.isEnabled = habilitar
        actualizarColorBotones(habilitar)
    }

    // [Actualizar estilo visual de los botones]
    private fun actualizarColorBotones(habilitar: Boolean) {
        val colorTexto = if (habilitar) android.R.color.white else android.R.color.darker_gray
        binding.btnLogin.setTextColor(resources.getColor(colorTexto, null))
        binding.tvRegister.setTextColor(resources.getColor(colorTexto, null))
    }

    // [Navegar a la pantalla principal]
    private fun navegarAHU2() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
