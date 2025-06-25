package com.sigmas.dogapp.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sigmas.dogapp.ViewModel.HomeViewModel
import com.sigmas.dogapp.databinding.FragmentHomeBinding
import com.sigmas.dogapp.ui.Home.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()


    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar RecyclerView con el adaptador
        adapter = HomeAdapter(emptyList()) { citaSeleccionada ->
            // Aquí puedes abrir detalle, editar o eliminar
        }

        binding.recyclerViewCitas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }

        // Observar las citas
        viewModel.citas.observe(viewLifecycleOwner) { listaCitas ->
            adapter.updateList(listaCitas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

