package com.sigmas.dogapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.sigmas.dogapp.view.Ui.Home.HomeActivity
import com.sigmas.dogapp.R
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

// [Login con autenticación biométrica] (Pantalla de inicio de sesión con lector de huella usando BiometricPrompt)
class LoginActivity : AppCompatActivity() {

    // [Animación de huella digital] (Vista de animación Lottie para mostrar el ícono de autenticación)
    private lateinit var fingerprintAninamtion: LottieAnimationView

    // [Prompt de autenticación biométrica] (Gestor del cuadro de diálogo y configuración para la autenticación)
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // [Inicializa la animación de huella] (Se obtiene la referencia desde el layout)
        fingerprintAninamtion = findViewById(R.id.fingerprintAnimation)

        // [Click en la animación de huella] (Dispara el diálogo de autenticación biométrica)
        fingerprintAninamtion.setOnClickListener {
            mostrarDialogoBiometrico()
        }
    }

    // [Mostrar diálogo biométrico] (Inicia la autenticación con huella digital)
    private fun mostrarDialogoBiometrico() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // [Autenticación exitosa] (Navega a la actividad principal tras autenticarse correctamente)
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navegarAHU2()
                }

                // [Error en autenticación] (Muestra un mensaje cuando ocurre un error, como cancelación)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                // [Fallo de autenticación] (Huella no reconocida)
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        // [Configuración del cuadro biométrico] (Define el contenido textual del diálogo de autenticación)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Coloca tu dedo en el sensor")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // [Navegar a Home] (Después de la autenticación exitosa, abre la pantalla principal)
    private fun navegarAHU2() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
