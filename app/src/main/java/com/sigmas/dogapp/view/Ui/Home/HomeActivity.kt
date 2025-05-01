package com.sigmas.dogapp.view.Ui.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sigmas.dogapp.R
import com.sigmas.dogapp.view.Data.Model.Cita
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import com.sigmas.dogapp.view.Ui.Home.HomeAdapter
import com.sigmas.dogapp.view.Ui.Newappointment.NewAppointmentActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerViewCitas: RecyclerView
    private lateinit var fabAddCita: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerViewCitas = findViewById(R.id.recyclerViewCitas)
        fabAddCita = findViewById(R.id.fabAddCita)

        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(getFakeCitas()) { Cita ->
            // Funcionalidad de las citas
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

    private fun getFakeCitas(): List<Cita> {
        return listOf(
            Cita(1, "Firulais", "No come", 1),
            Cita(2, "Boby", "Tiene pulgas", 2),
            Cita(3, "Max", "Solo duerme", 3),
            Cita(4,"Amarillo","come mucho",4)
        )
    }
}