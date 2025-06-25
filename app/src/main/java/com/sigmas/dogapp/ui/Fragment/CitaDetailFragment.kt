package com.sigmas.dogapp.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sigmas.dogapp.UI.Fragments.CitaDetailFragmentArgs
import com.sigmas.dogapp.ViewModel.CitaDetailViewModel
import com.sigmas.dogapp.databinding.FragmentCitaDetailBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class CitaDetailFragment : Fragment() {

    private val viewModel: CitaDetailViewModel by viewModels()

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

        val citaId = args.citaId.toString()
        viewModel.loadCitaById(citaId)

        viewModel.cita.observe(viewLifecycleOwner) { cita ->
            cita?.let {
                binding.TituloNombreMascota.text = it.nombreMascota
                binding.DetalleTurno.text = "Turno: ${it.turno}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
