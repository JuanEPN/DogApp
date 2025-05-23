package com.sigmas.dogapp.view.Ui.Home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sigmas.dogapp.R
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import com.sigmas.dogapp.view.Ui.Newappointment.NewAppointmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import com.sigmas.dogapp.view.Ui.CitaDetail.CitaDetailActivity

class HomeActivity : AppCompatActivity() {

    // [Variables de UI y lógica] (Referencias al RecyclerView, botón flotante y repositorio de citas)
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerViewCitas: RecyclerView
    private lateinit var fabAddCita: FloatingActionButton
    private lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // [Forzar modo claro] (Desactiva el modo oscuro para evitar errores visuales)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // [Inicializar vistas] (Conecta las vistas XML con variables de Kotlin)
        recyclerViewCitas = findViewById(R.id.recyclerViewCitas)
        fabAddCita = findViewById(R.id.fabAddCita)

        // [Inicializar base de datos y repositorio] (Acceso a la base de datos local usando Room)
        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        // [Configurar componentes de UI] (Inicializa RecyclerView, botón flotante y carga de datos)
        setupRecyclerView()
        setupFab()
        cargarCitasDesdeBD()
    }

    // [Configuración del RecyclerView] (Muestra la lista de citas y gestiona el clic en un ítem)
    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(emptyList()) { cita ->
            val intent = Intent(this, CitaDetailActivity::class.java)
            intent.putExtra("id", cita.id)
            startActivity(intent)
        }
        recyclerViewCitas.layoutManager = LinearLayoutManager(this)
        recyclerViewCitas.adapter = homeAdapter
    }

    // [Configuración del botón flotante] (Abre la actividad para registrar una nueva cita)
    private fun setupFab() {
        fabAddCita.setOnClickListener {
            val intent = Intent(this, NewAppointmentActivity::class.java)
            startActivity(intent)
        }
    }

    // [Cargar datos de la base de datos] (Obtiene las citas y actualiza el adaptador con la lista)
    private fun cargarCitasDesdeBD() {
        lifecycleScope.launch {
            val citas = citaRepository.obtenerTodasLasCitas()
            homeAdapter.updateList(citas)
        }
    }
}
