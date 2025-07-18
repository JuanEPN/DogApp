package com.sigmas.dogapp.ui.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sigmas.dogapp.databinding.FragmentEditarCitaBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditarCitaFragment: Fragment() {

    private var _binding: FragmentEditarCitaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}