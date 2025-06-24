package com.sigmas.dogapp.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.sigmas.dogapp.Data.CitaDatabase
import com.sigmas.dogapp.UI.Fragments.CitaDetailFragmentArgs
import com.sigmas.dogapp.databinding.FragmentCitaDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CitaDetailFragment : Fragment() {

    private var _binding: FragmentCitaDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CitaDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = CitaDatabase.getDatabase(requireContext()).citaDao()
        val citaId = args.citaId

        lifecycleScope.launch {
            val cita = withContext(Dispatchers.IO) {
                dao.getAppointmentId(citaId)
            }

            cita?.let {
                binding.TituloNombreMascota.text = it.nombreMascota
                binding.DetalleTurno.text = "Turno: ${it.turno}"
                // Agregá más si los tenés definidos en el layout:
                // binding.TvRaza.text = it.raza
                // binding.TvSintomas.text = it.sintomas ?: "Sin síntomas"
                // binding.TvNombrePropietario.text = it.nombrePropietario
                // binding.TvTelefono.text = it.telefono
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
