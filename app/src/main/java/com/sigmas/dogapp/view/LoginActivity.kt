package com.sigmas.dogapp.view
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.sigmas.dogapp.view.HomeActivity //Siguiente actividad a ir
import com.sigmas.dogapp.R
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat



class LoginActivity : AppCompatActivity() { // Declaramos una nueva actividad que sera la del Login. AppCompatActivity es una clase que nos da mayor compatibilidad con versiones anteriores

    private lateinit var fingerprintAninamtion: LottieAnimationView // Aca creamos una variable privada que nos ayuda a acceder a la animacion de la huella en el XML, ademas que se
    // Inicializará después con lateinit

    override fun onCreate(savedInstanceState : Bundle?) { //Este metodo que hicimos es el encargado de cargar el XML de esta actividad en este caso Login
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fingerprintAninamtion = findViewById(R.id.fingerprintAnimation) //Aca conectamos la clase con el archivo XML buscandolo por su id

        fingerprintAninamtion.setOnClickListener{ //Ponemos un metodo que este pendiente si se da click a la animacion para mostrar el dialogo biometrico osea su funcion
            mostrarDialogoBiometrico()
        }

    }

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private fun mostrarDialogoBiometrico(){ //Esta funcion nos muestra el dialogo que mencione anteriormente para cuando se use la huella digital
        val executor = ContextCompat.getMainExecutor(this) //Crea un ejecutor que maneja los callbacks del sistema biometrico para esta actividad
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback()  {//Esto maneja la logica de autenticacion
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { //Funcion que se llama cuando la autenticacion fue exitosa
                super.onAuthenticationSucceeded(result)
                navegarAHU2()//Navega a Home que es nuestra siguiente actividad
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { //Esta funcion se llama cuando el usuario cancelo
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() { //Funcion de cuando se pone un huella incorrecta
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder() //Aca defino la informacion que mostrara el cuadro de biometria
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Coloca tu dedo en el sensor")
            .setNegativeButtonText("Cancelar")
            .build()//Este metodo lo construye

        biometricPrompt.authenticate(promptInfo)
    }

    private fun navegarAHU2(){ //Esta funcion que hemos creado abre la siguiente pantalla o actividad y cierra esta
        val intent = Intent(this, HomeActivity::class.java) //El Intent es quien le avisa a Android de cambiar de actividad
        startActivity(intent)
        finish()
    }
}