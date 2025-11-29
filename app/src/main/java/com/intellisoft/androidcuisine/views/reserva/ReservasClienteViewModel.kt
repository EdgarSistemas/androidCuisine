package com.intellisoft.androidcuisine.views.reserva

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.data.repository.ClienteRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.ClienteRepository
import kotlinx.coroutines.launch

class ReservasClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val clienteRepository: ClienteRepository = ClienteRepositoryImpl()

    private var clienteId: Int = 0

    // Selecciones del usuario (guardadas localmente en el ViewModel)
    private var _sucursalLocal: SucursalActivaDto? = null
    private var _areaLocal: AreaDto? = null
    private var _mesaLocal: MesaClienteDto? = null

    // ==================== SUCURSALES ====================

    private val _sucursalesState = MutableLiveData<SucursalesClienteState>()
    val sucursalesState: LiveData<SucursalesClienteState> = _sucursalesState

    private val _sucursalSeleccionada = MutableLiveData<SucursalActivaDto?>()
    val sucursalSeleccionada: LiveData<SucursalActivaDto?> = _sucursalSeleccionada

    // ==================== ÁREAS ====================

    private val _areasState = MutableLiveData<AreasClienteState>()
    val areasState: LiveData<AreasClienteState> = _areasState

    private val _areaSeleccionada = MutableLiveData<AreaDto?>()
    val areaSeleccionada: LiveData<AreaDto?> = _areaSeleccionada

    // ==================== MESAS ====================

    private val _mesasState = MutableLiveData<MesasClienteState>()
    val mesasState: LiveData<MesasClienteState> = _mesasState

    private val _mesaSeleccionada = MutableLiveData<MesaClienteDto?>()
    val mesaSeleccionada: LiveData<MesaClienteDto?> = _mesaSeleccionada

    // ==================== HOLD ====================

    private val _holdState = MutableLiveData<HoldClienteState>()
    val holdState: LiveData<HoldClienteState> = _holdState

    private val _holdActivo = MutableLiveData<HoldClienteDto?>()
    val holdActivo: LiveData<HoldClienteDto?> = _holdActivo

    // ==================== CONTADOR ====================

    private val _tiempoRestante = MutableLiveData<Long?>()
    val tiempoRestante: LiveData<Long?> = _tiempoRestante

    private val _contadorActivo = MutableLiveData<Boolean>(false)
    val contadorActivo: LiveData<Boolean> = _contadorActivo

    private var countDownTimer: CountDownTimer? = null

    // ==================== RESERVAS ====================

    private val _crearReservaState = MutableLiveData<CrearReservaClienteState>()
    val crearReservaState: LiveData<CrearReservaClienteState> = _crearReservaState

    private val _misReservasState = MutableLiveData<MisReservasState>()
    val misReservasState: LiveData<MisReservasState> = _misReservasState

    private val _cancelarReservaState = MutableLiveData<CancelarReservaClienteState>()
    val cancelarReservaState: LiveData<CancelarReservaClienteState> = _cancelarReservaState

    // ==================== CONFIGURACIÓN ====================

    fun configurarCliente(clienteId: Int) {
        this.clienteId = clienteId
    }

    fun getClienteId(): Int = clienteId

    // ==================== SUCURSALES ====================

    fun loadSucursalesActivas() {
        _sucursalesState.value = SucursalesClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getSucursalesActivas()

            result.fold(
                onSuccess = { sucursales ->
                    if (sucursales.isEmpty()) {
                        _sucursalesState.value = SucursalesClienteState.Empty
                    } else {
                        _sucursalesState.value = SucursalesClienteState.Success(sucursales)
                    }
                },
                onFailure = { exception ->
                    _sucursalesState.value = SucursalesClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun seleccionarSucursal(sucursal: SucursalActivaDto) {
        _sucursalLocal = sucursal
        _sucursalSeleccionada.value = sucursal
        // Limpiar selecciones dependientes
        _areaLocal = null
        _mesaLocal = null
        _areaSeleccionada.value = null
        _mesaSeleccionada.value = null
    }

    fun getSucursalValue(): SucursalActivaDto? = _sucursalLocal

    // ==================== ÁREAS ====================

    fun loadAreas() {
        val sucursalId = _sucursalLocal?.id_sucursal ?: return
        _areasState.value = AreasClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getAreas(sucursalId)

            result.fold(
                onSuccess = { areas ->
                    if (areas.isEmpty()) {
                        _areasState.value = AreasClienteState.Empty
                    } else {
                        _areasState.value = AreasClienteState.Success(areas)
                    }
                },
                onFailure = { exception ->
                    _areasState.value = AreasClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun seleccionarArea(area: AreaDto?) {
        _areaLocal = area
        _areaSeleccionada.value = area
        _mesaLocal = null
        _mesaSeleccionada.value = null
    }

    fun getAreaValue(): AreaDto? = _areaLocal

    // ==================== MESAS ====================

    fun loadMesas() {
        val sucursalId = _sucursalLocal?.id_sucursal ?: return
        _mesasState.value = MesasClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.getMesas(sucursalId, _areaLocal?.id_area)

            result.fold(
                onSuccess = { mesas ->
                    if (mesas.isEmpty()) {
                        _mesasState.value = MesasClienteState.Empty
                    } else {
                        _mesasState.value = MesasClienteState.Success(mesas)
                    }
                },
                onFailure = { exception ->
                    _mesasState.value = MesasClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun seleccionarMesa(mesa: MesaClienteDto) {
        _mesaLocal = mesa
        _mesaSeleccionada.value = mesa
    }

    fun getMesaValue(): MesaClienteDto? = _mesaLocal

    // ==================== CONTADOR ====================

    fun iniciarContadorSiNoActivo() {
        if (_contadorActivo.value == true) return
        iniciarContador()
    }

    private fun iniciarContador() {
        detenerContador()
        _contadorActivo.value = true

        countDownTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _tiempoRestante.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _tiempoRestante.value = 0
                _contadorActivo.value = false
                _holdActivo.value = null
                _holdState.value = HoldClienteState.Expired
            }
        }.start()
    }

    fun detenerContador() {
        countDownTimer?.cancel()
        countDownTimer = null
        _tiempoRestante.value = null
        _contadorActivo.value = false
    }

    // ==================== HOLDS ====================

    fun crearHold(inicio: String, fin: String) {
        val mesa = _mesaLocal ?: run {
            _holdState.value = HoldClienteState.Error("Selecciona una mesa primero")
            return
        }

        _holdState.value = HoldClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.crearHold(mesa.id_mesa, inicio, fin)

            result.fold(
                onSuccess = { hold ->
                    _holdActivo.value = hold
                    _holdState.value = HoldClienteState.Success(hold)
                },
                onFailure = { exception ->
                    _holdState.value = HoldClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun cancelarHoldActivo() {
        val hold = _holdActivo.value ?: return

        viewModelScope.launch {
            clienteRepository.cancelarHold(hold.id_hold)
            _holdActivo.value = null
        }
    }

    // ==================== RESERVAS ====================

    fun crearReserva(inicio: String, finEstimado: String, notas: String? = null) {
        val hold = _holdActivo.value ?: run {
            _crearReservaState.value = CrearReservaClienteState.Error("No hay hold activo")
            return
        }

        _crearReservaState.value = CrearReservaClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.crearReserva(
                clienteId = clienteId,
                holdId = hold.id_hold,
                inicio = inicio,
                finEstimado = finEstimado,
                toleranciaMin = 15,
                notas = notas
            )

            result.fold(
                onSuccess = { reserva ->
                    detenerContador()
                    _holdActivo.value = null
                    _crearReservaState.value = CrearReservaClienteState.Success(reserva)
                },
                onFailure = { exception ->
                    _crearReservaState.value = CrearReservaClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun loadMisReservas(estado: Int? = null) {
        _misReservasState.value = MisReservasState.Loading

        viewModelScope.launch {
            val result = clienteRepository.listarReservas(clienteId, estado)

            result.fold(
                onSuccess = { reservas ->
                    if (reservas.isEmpty()) {
                        _misReservasState.value = MisReservasState.Empty
                    } else {
                        _misReservasState.value = MisReservasState.Success(reservas)
                    }
                },
                onFailure = { exception ->
                    _misReservasState.value = MisReservasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun cancelarReserva(reservaId: Int, motivo: String? = null) {
        _cancelarReservaState.value = CancelarReservaClienteState.Loading

        viewModelScope.launch {
            val result = clienteRepository.cancelarReserva(reservaId, motivo)

            result.fold(
                onSuccess = { mensaje ->
                    _cancelarReservaState.value = CancelarReservaClienteState.Success(mensaje)
                },
                onFailure = { exception ->
                    _cancelarReservaState.value = CancelarReservaClienteState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    // ==================== RESETS ====================

    fun resetCrearReservaState() {
        _crearReservaState.value = CrearReservaClienteState.Idle
    }

    fun resetCancelarReservaState() {
        _cancelarReservaState.value = CancelarReservaClienteState.Idle
    }

    fun resetParaNuevaReserva() {
        detenerContador()
        cancelarHoldActivo()
        _mesaLocal = null
        _mesaSeleccionada.value = null
        _holdActivo.value = null
        _holdState.value = HoldClienteState.Idle
    }

    fun resetTodo() {
        resetParaNuevaReserva()
        _sucursalLocal = null
        _areaLocal = null
        _sucursalSeleccionada.value = null
        _areaSeleccionada.value = null
    }

    override fun onCleared() {
        super.onCleared()
        detenerContador()
    }
}

// ==================== STATES ====================

sealed class SucursalesClienteState {
    object Loading : SucursalesClienteState()
    object Empty : SucursalesClienteState()
    data class Success(val sucursales: List<SucursalActivaDto>) : SucursalesClienteState()
    data class Error(val message: String) : SucursalesClienteState()
}

sealed class AreasClienteState {
    object Loading : AreasClienteState()
    object Empty : AreasClienteState()
    data class Success(val areas: List<AreaDto>) : AreasClienteState()
    data class Error(val message: String) : AreasClienteState()
}

sealed class MesasClienteState {
    object Loading : MesasClienteState()
    object Empty : MesasClienteState()
    data class Success(val mesas: List<MesaClienteDto>) : MesasClienteState()
    data class Error(val message: String) : MesasClienteState()
}

sealed class HoldClienteState {
    object Idle : HoldClienteState()
    object Loading : HoldClienteState()
    object Expired : HoldClienteState()
    data class Success(val hold: HoldClienteDto) : HoldClienteState()
    data class Error(val message: String) : HoldClienteState()
}

sealed class CrearReservaClienteState {
    object Idle : CrearReservaClienteState()
    object Loading : CrearReservaClienteState()
    data class Success(val reserva: ReservaClienteDto) : CrearReservaClienteState()
    data class Error(val message: String) : CrearReservaClienteState()
}

sealed class MisReservasState {
    object Loading : MisReservasState()
    object Empty : MisReservasState()
    data class Success(val reservas: List<ReservaClienteDto>) : MisReservasState()
    data class Error(val message: String) : MisReservasState()
}

sealed class CancelarReservaClienteState {
    object Idle : CancelarReservaClienteState()
    object Loading : CancelarReservaClienteState()
    data class Success(val mensaje: String) : CancelarReservaClienteState()
    data class Error(val message: String) : CancelarReservaClienteState()
}