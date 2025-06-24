package com.sigmas.dogapp.ui.Login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sigmas.dogapp.R
import com.sigmas.dogapp.ui.Home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Referencias
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.isEnabled = false
        tvRegister.isEnabled = false

        // Validación en tiempo real
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val camposLlenos = etEmail.text.isNotBlank() && etPassword.text.isNotBlank()
                btnLogin.isEnabled = camposLlenos
                tvRegister.isEnabled = camposLlenos
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        // Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (password.length != 6 || !password.all { it.isDigit() }) {
                mostrarDialogo("Error", "La contraseña debe tener exactamente 6 números.")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    navegarAHU2()
                }
                .addOnFailureListener {
                    mostrarDialogo("Login incorrecto", "Email o contraseña inválidos.")
                }
        }

        // Registro
        tvRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (password.length != 6 || !password.all { it.isDigit() }) {
                mostrarDialogo("Error", "La contraseña debe tener exactamente 6 números.")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    navegarAHU2()
                }
                .addOnFailureListener { e ->
                    if (e.message?.contains("already") == true || e.message?.contains("email address") == true) {
                        mostrarDialogo("Usuario ya registrado", "Ya existe una cuenta con este correo.")
                    } else {
                        mostrarDialogo("Error en el registro", e.message ?: "Error desconocido.")
                    }
                }
        }
    }

    private fun navegarAHU2() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}