package com.sigmas.dogapp.Ui.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.sigmas.dogapp.R
import com.sigmas.dogapp.Ui.Home.HomeActivity

// [Login con autenticación biométrica] (Pantalla de inicio de sesión con lector de huella usando BiometricPrompt)
class LoginActivity : AppCompatActivity() {

    private lateinit var fingerprintAnimation: LottieAnimationView
    private lateinit var btnAcceder: Button

    // Prompt de autenticación biométrica
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias UI
        fingerprintAnimation = findViewById(R.id.fingerprintAnimation)
        btnAcceder = findViewById(R.id.btnAcceder)

        // Click animación de huella
        fingerprintAnimation.setOnClickListener {
            mostrarDialogoBiometrico()
        }

        // Click botón para acceder sin huella
        btnAcceder.setOnClickListener {
            navegarAHU2()
        }
    }

    // Mostrar diálogo de autenticación biométrica
    private fun mostrarDialogoBiometrico() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navegarAHU2()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        // Configurar diálogo
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Coloca tu dedo en el sensor")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // Navegar a HomeActivity
    private fun navegarAHU2() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}