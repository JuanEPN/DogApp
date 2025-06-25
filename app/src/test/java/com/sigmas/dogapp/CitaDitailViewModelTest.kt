package com.sigmas.dogapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Repository.CitaRepository
import com.sigmas.dogapp.ViewModel.CitaDetailViewModel
import com.sigmas.dogapp.testutils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CitaDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: CitaRepository
    private lateinit var viewModel: CitaDetailViewModel

    @Before
    fun setup() {
        repository = mock()
        viewModel = CitaDetailViewModel(repository)
    }

    @Test
    fun `loadCitaById actualiza LiveData con cita retornada del repositorio`() = runTest {

        val citaEsperada = Cita(id = "123", nombrePropietario = "Juan", nombreMascota = "Toby", raza = "Labrador", telefono = "3001234567", sintomas = "Fiebre", imagenUrl = "https://miservidor.com/toby.jpg", turno = 1 )
        whenever(repository.getCitaById("123")).thenReturn(citaEsperada)

        viewModel.loadCitaById("123")

        val resultado = viewModel.cita.getOrAwaitValue()
        assertEquals(citaEsperada, resultado)
    }

    @Test
    fun `deleteCita llama a repository deleteCita`() = runTest {
        val citaAEliminar = Cita(
            id = "456",
            nombrePropietario = "Laura",
            nombreMascota = "Milo",
            raza = "Pug",
            telefono = "3124567890",
            sintomas = "Tos",
            imagenUrl = "https://imagen.com/perro.jpg",
            turno = 2
        )

        viewModel.deleteCita(citaAEliminar)

        verify(repository).deleteCita(citaAEliminar)
    }
}
