package com.sigmas.dogapp.ui.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sigmas.dogapp.R
import com.sigmas.dogapp.ui.Login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar toolbar
        supportActionBar?.hide()

        // Asignar el layout
        setContentView(R.layout.activity_splash)

        // Esperar 5 segundos y navegar a LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Impide volver con botón atrás
        }, 5000)
    }
}