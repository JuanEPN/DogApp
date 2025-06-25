package com.sigmas.dogapp.ui.Home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sigmas.dogapp.R
import com.sigmas.dogapp.Data.AppDatabase
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.ui.CitaDetail.CitaDetailActivity
import com.sigmas.dogapp.ui.Newappointment.NewAppointmentActivity
import com.sigmas.dogapp.ViewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerViewCitas: RecyclerView
    private lateinit var fabAddCita: FloatingActionButton
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        recyclerViewCitas = findViewById(R.id.recyclerViewCitas)
        fabAddCita = findViewById(R.id.fabAddCita)

        setupRecyclerView()
        setupFab()

        viewModel.citas.observe(this) { citas ->
            homeAdapter.updateList(citas)
        }
    }


    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(emptyList()) { cita ->
            val intent = Intent(this, CitaDetailActivity::class.java)
            intent.putExtra("id", cita.id)
            startActivity(intent)
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
}