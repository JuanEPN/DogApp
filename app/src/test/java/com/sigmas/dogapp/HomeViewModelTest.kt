package com.sigmas.dogapp
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Repository.CitaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.verify
import com.sigmas.dogapp.testutils.MainDispatcherRule
import com.sigmas.dogapp.ViewModel.HomeViewModel
import org.junit.Before
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CitaRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        repository = mock()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `insertarCita llama al repositorio insertCita`() = runTest {
        val cita = Cita(
            id = "101",
            nombrePropietario = "Ana",
            nombreMascota = "Luna",
            raza = "Boxer",
            telefono = "3001112233",
            sintomas = "Vómito",
            imagenUrl = "https://dogapi.com/luna.jpg",
            turno = 4
        )


        viewModel.insertarCita(cita)

        verify(repository).insertCita(cita)
    }

    @Test
    fun `eliminarCita llama al repositorio deleteCita`() = runTest {

        val cita = Cita(
            id = "202",
            nombrePropietario = "Carlos",
            nombreMascota = "Rocky",
            raza = "Pitbull",
            telefono = "3214567890",
            sintomas = "Herida en pata",
            imagenUrl = "",
            turno = 6
        )

        viewModel.eliminarCita(cita)


    }
}
