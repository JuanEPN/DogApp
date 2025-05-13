package com.sigmas.dogapp.view.Ui.Home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sigmas.dogapp.R
import com.sigmas.dogapp.view.Data.AppDatabase
import com.sigmas.dogapp.view.Ui.Repository.CitaRepository
import com.sigmas.dogapp.view.Ui.Newappointment.NewAppointmentActivity
import com.sigmas.dogapp.view.Data.Model.Cita
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerViewCitas: RecyclerView
    private lateinit var fabAddCita: FloatingActionButton
    private lateinit var citaRepository: CitaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerViewCitas = findViewById(R.id.recyclerViewCitas)
        fabAddCita = findViewById(R.id.fabAddCita)

        val database = AppDatabase.getDatabase(applicationContext)
        citaRepository = CitaRepository(database.citaDao())

        setupRecyclerView()
        setupFab()
        cargarCitasDesdeBD()
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(emptyList()) { cita ->
            // Acción al hacer clic (opcional)
        }
        recyclerViewCitas.layoutManager = LinearLayoutManager(this)
        recyclerViewCitas.adapter = homeAdapter
    }

    private fun setupFab() {
        fabAddCita.setOnClickListener {
            val intent = Intent(this, NewAppointmentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarCitasDesdeBD() {
        lifecycleScope.launch {
            val citas = citaRepository.obtenerTodasLasCitas()
            homeAdapter.updateList(citas)
        }
    }
}