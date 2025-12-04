package com.intellisoft.androidcuisine.views.marketing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.campania.CampaniaRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.campania.CampaniaRepository
import kotlinx.coroutines.launch

class MarketingViewModel(application: Application) : AndroidViewModel(application) {

    private val campaniaRepository: CampaniaRepository = CampaniaRepositoryImpl()

    // Campañas
    private val _campaniasState = MutableLiveData<CampaniasState>()
    val campaniasState: LiveData<CampaniasState> = _campaniasState

    private val _crearCampaniaState = MutableLiveData<CrearCampaniaState>()
    val crearCampaniaState: LiveData<CrearCampaniaState> = _crearCampaniaState

    // Métricas
    private val _clientesVIPState = MutableLiveData<ClientesVIPState>()
    val clientesVIPState: LiveData<ClientesVIPState> = _clientesVIPState

    private val _clientesFrecuentesState = MutableLiveData<ClientesFrecuentesState>()
    val clientesFrecuentesState: LiveData<ClientesFrecuentesState> = _clientesFrecuentesState

    private val _clientesInactivosState = MutableLiveData<ClientesInactivosState>()
    val clientesInactivosState: LiveData<ClientesInactivosState> = _clientesInactivosState

    private val _clientesNuevosState = MutableLiveData<ClientesNuevosState>()
    val clientesNuevosState: LiveData<ClientesNuevosState> = _clientesNuevosState

    private val _clientesCanalState = MutableLiveData<ClientesCanalState>()
    val clientesCanalState: LiveData<ClientesCanalState> = _clientesCanalState

    private val _generarCampaniaState = MutableLiveData<GenerarCampaniaState>()
    val generarCampaniaState: LiveData<GenerarCampaniaState> = _generarCampaniaState

    // Campañas
    fun loadCampanias() {
        _campaniasState.value = CampaniasState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getCampanias()

            result.fold(
                onSuccess = { campanias ->
                    if (campanias.isEmpty()) {
                        _campaniasState.value = CampaniasState.Empty
                    } else {
                        _campaniasState.value = CampaniasState.Success(campanias)
                    }
                },
                onFailure = { exception ->
                    _campaniasState.value = CampaniasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearCampania(request: CrearCampaniaRequest) {
        _crearCampaniaState.value = CrearCampaniaState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.crearCampania(request)

            result.fold(
                onSuccess = { campania ->
                    _crearCampaniaState.value = CrearCampaniaState.Success(campania.codigo)
                },
                onFailure = { exception ->
                    _crearCampaniaState.value = CrearCampaniaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun activarCampania(id: Int) {
        viewModelScope.launch {
            campaniaRepository.activarCampania(id)
            loadCampanias()
        }
    }

    fun desactivarCampania(id: Int) {
        viewModelScope.launch {
            campaniaRepository.desactivarCampania(id)
            loadCampanias()
        }
    }

    // Métricas
    fun loadClientesVIP(topN: Int = 20) {
        _clientesVIPState.value = ClientesVIPState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getClientesVIP(topN)

            result.fold(
                onSuccess = { clientes ->
                    if (clientes.isEmpty()) {
                        _clientesVIPState.value = ClientesVIPState.Empty
                    } else {
                        _clientesVIPState.value = ClientesVIPState.Success(clientes)
                    }
                },
                onFailure = { exception ->
                    _clientesVIPState.value = ClientesVIPState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadClientesFrecuentes(topN: Int = 20) {
        _clientesFrecuentesState.value = ClientesFrecuentesState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getClientesFrecuentes(topN)

            result.fold(
                onSuccess = { clientes ->
                    if (clientes.isEmpty()) {
                        _clientesFrecuentesState.value = ClientesFrecuentesState.Empty
                    } else {
                        _clientesFrecuentesState.value = ClientesFrecuentesState.Success(clientes)
                    }
                },
                onFailure = { exception ->
                    _clientesFrecuentesState.value = ClientesFrecuentesState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadClientesInactivos(dias: Int = 30) {
        _clientesInactivosState.value = ClientesInactivosState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getClientesInactivos(dias)

            result.fold(
                onSuccess = { clientes ->
                    if (clientes.isEmpty()) {
                        _clientesInactivosState.value = ClientesInactivosState.Empty
                    } else {
                        _clientesInactivosState.value = ClientesInactivosState.Success(clientes)
                    }
                },
                onFailure = { exception ->
                    _clientesInactivosState.value = ClientesInactivosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadClientesNuevos(dias: Int = 30) {
        _clientesNuevosState.value = ClientesNuevosState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getClientesNuevos(dias)

            result.fold(
                onSuccess = { clientes ->
                    if (clientes.isEmpty()) {
                        _clientesNuevosState.value = ClientesNuevosState.Empty
                    } else {
                        _clientesNuevosState.value = ClientesNuevosState.Success(clientes)
                    }
                },
                onFailure = { exception ->
                    _clientesNuevosState.value = ClientesNuevosState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadClientesPorCanal() {
        _clientesCanalState.value = ClientesCanalState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.getClientesPorCanal()

            result.fold(
                onSuccess = { clientes ->
                    if (clientes.isEmpty()) {
                        _clientesCanalState.value = ClientesCanalState.Empty
                    } else {
                        _clientesCanalState.value = ClientesCanalState.Success(clientes)
                    }
                },
                onFailure = { exception ->
                    _clientesCanalState.value = ClientesCanalState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun generarCampaniaDesdeMetrica(request: GenerarCampaniaMetricaRequest) {
        _generarCampaniaState.value = GenerarCampaniaState.Loading

        viewModelScope.launch {
            val result = campaniaRepository.generarCampaniaDesdeMetrica(request)

            result.fold(
                onSuccess = { response ->
                    _generarCampaniaState.value = GenerarCampaniaState.Success(response)
                },
                onFailure = { exception ->
                    _generarCampaniaState.value = GenerarCampaniaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun resetCrearCampaniaState() {
        _crearCampaniaState.value = CrearCampaniaState.Idle
    }

    fun resetGenerarCampaniaState() {
        _generarCampaniaState.value = GenerarCampaniaState.Idle
    }
}

// States
sealed class CampaniasState {
    object Loading : CampaniasState()
    object Empty : CampaniasState()
    data class Success(val campanias: List<CampaniaDto>) : CampaniasState()
    data class Error(val message: String) : CampaniasState()
}

sealed class CrearCampaniaState {
    object Idle : CrearCampaniaState()
    object Loading : CrearCampaniaState()
    data class Success(val codigo: String) : CrearCampaniaState()
    data class Error(val message: String) : CrearCampaniaState()
}

sealed class ClientesVIPState {
    object Loading : ClientesVIPState()
    object Empty : ClientesVIPState()
    data class Success(val clientes: List<ClienteVIPDto>) : ClientesVIPState()
    data class Error(val message: String) : ClientesVIPState()
}

sealed class ClientesFrecuentesState {
    object Loading : ClientesFrecuentesState()
    object Empty : ClientesFrecuentesState()
    data class Success(val clientes: List<ClienteFrecuenteDto>) : ClientesFrecuentesState()
    data class Error(val message: String) : ClientesFrecuentesState()
}

sealed class ClientesInactivosState {
    object Loading : ClientesInactivosState()
    object Empty : ClientesInactivosState()
    data class Success(val clientes: List<ClienteInactivoDto>) : ClientesInactivosState()
    data class Error(val message: String) : ClientesInactivosState()
}

sealed class ClientesNuevosState {
    object Loading : ClientesNuevosState()
    object Empty : ClientesNuevosState()
    data class Success(val clientes: List<ClienteNuevoDto>) : ClientesNuevosState()
    data class Error(val message: String) : ClientesNuevosState()
}

sealed class ClientesCanalState {
    object Loading : ClientesCanalState()
    object Empty : ClientesCanalState()
    data class Success(val clientes: List<ClienteCanalDto>) : ClientesCanalState()
    data class Error(val message: String) : ClientesCanalState()
}

sealed class GenerarCampaniaState {
    object Idle : GenerarCampaniaState()
    object Loading : GenerarCampaniaState()
    data class Success(val response: GenerarCampaniaMetricaResponse) : GenerarCampaniaState()
    data class Error(val message: String) : GenerarCampaniaState()
}