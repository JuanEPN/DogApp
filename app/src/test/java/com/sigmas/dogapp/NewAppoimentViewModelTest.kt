package com.sigmas.dogapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Network.DogApiService
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.ViewModel.NewAppointmentViewModel
import com.sigmas.dogapp.testutils.MainDispatcherRule
import com.sigmas.dogapp.testutils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class NewAppointmentViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CitaRepository
    private lateinit var dogApiService: DogApiService
    private lateinit var viewModel: NewAppointmentViewModel

    @Before
    fun setup() {
        repository = mock()
        dogApiService = mock()
        viewModel = NewAppointmentViewModel(repository, dogApiService)
    }

    @Test
    fun `guardarCita actualiza citaGuardada con true al guardar exitosamente`() = runTest {
        // Arrange
        val cita = Cita(
            id = "001",
            nombrePropietario = "Juan",
            nombreMascota = "Max",
            raza = "Beagle",
            telefono = "3011122233",
            sintomas = "Fiebre",
            imagenUrl = "https://dogapi.com/max.jpg",
            turno = 3
        )


        viewModel.guardarCita(cita)

        val resultado = viewModel.citaGuardada.getOrAwaitValue()
        assertEquals(true, resultado)
    }

    @Test
    fun `guardarCita actualiza citaGuardada con false al fallar`() = runTest {

        val cita = Cita(
            id = "002",
            nombrePropietario = "Laura",
            nombreMascota = "Nina",
            raza = "Chihuahua",
            telefono = "3009876543",
            sintomas = "Letargo",
            imagenUrl = "",
            turno = 7
        )

        whenever(repository.insertCita(cita)).thenThrow(RuntimeException("Error de base de datos"))

        viewModel.guardarCita(cita)

        val resultado = viewModel.citaGuardada.getOrAwaitValue()
        assertEquals(false, resultado)
    }



}
