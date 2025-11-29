package com.intellisoft.androidcuisine.views.reserva

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.data.repository.ReservasRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.ReservasRepository
import kotlinx.coroutines.launch

enum class TipoActor(val valor: Int) {
    CLIENTE(1),
    RECEPCIONISTA(2)
}

class ReservasViewModel(application: Application) : AndroidViewModel(application) {

    private val reservasRepository: ReservasRepository = ReservasRepositoryImpl()

    // Tipo de actor actual
    private var tipoActor: TipoActor = TipoActor.RECEPCIONISTA
    private var usuarioId: Int = 0

    // Mesas
    private val _mesasState = MutableLiveData<MesasState>()
    val mesasState: LiveData<MesasState> = _mesasState

    // Hold
    private val _holdState = MutableLiveData<HoldState>()
    val holdState: LiveData<HoldState> = _holdState

    private val _holdActivo = MutableLiveData<HoldDto?>()
    val holdActivo: LiveData<HoldDto?> = _holdActivo

    // Contador - Independiente del Hold
    private val _tiempoRestante = MutableLiveData<Long?>()
    val tiempoRestante: LiveData<Long?> = _tiempoRestante

    private val _contadorActivo = MutableLiveData<Boolean>(false)
    val contadorActivo: LiveData<Boolean> = _contadorActivo

    private var countDownTimer: CountDownTimer? = null

    // Reservas
    private val _reservasState = MutableLiveData<ReservasState>()
    val reservasState: LiveData<ReservasState> = _reservasState

    private val _crearReservaState = MutableLiveData<CrearReservaState>()
    val crearReservaState: LiveData<CrearReservaState> = _crearReservaState

    private val _accionReservaState = MutableLiveData<AccionReservaState>()
    val accionReservaState: LiveData<AccionReservaState> = _accionReservaState

    fun configurarActor(tipoActor: TipoActor, usuarioId: Int) {
        this.tipoActor = tipoActor
        this.usuarioId = usuarioId
    }

    fun getTipoActor(): TipoActor = tipoActor
    fun getUsuarioId(): Int = usuarioId
    fun esCliente(): Boolean = tipoActor == TipoActor.CLIENTE

    // ==================== CONTADOR ====================

    /**
     * Inicia el contador de 3 minutos.
     * Solo se llama al entrar al fragment de Nueva Reserva.
     * NO se reinicia al cambiar mesa o datos.
     */
    fun iniciarContadorSiNoActivo() {
        if (_contadorActivo.value == true) {
            // Ya hay un contador activo, no hacer nada
            return
        }
        iniciarContador()
    }

    private fun iniciarContador() {
        detenerContador()

        _contadorActivo.value = true

        // 3 minutos = 180,000 ms
        countDownTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _tiempoRestante.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _tiempoRestante.value = 0
                _contadorActivo.value = false
                _holdActivo.value = null
                _holdState.value = HoldState.Expired
            }
        }.start()
    }

    fun detenerContador() {
        countDownTimer?.cancel()
        countDownTimer = null
        _tiempoRestante.value = null
        _contadorActivo.value = false
    }

    fun resetearParaNuevaReserva() {
        detenerContador()
        _holdActivo.value = null
        _holdState.value = HoldState.Idle
        // El contador se reiniciará cuando el fragment llame a iniciarContadorSiNoActivo()
    }

    // ==================== MESAS ====================

    fun loadMesas(sucursalId: Int) {
        _mesasState.value = MesasState.Loading

        viewModelScope.launch {
            val result = reservasRepository.getMesas(sucursalId)

            result.fold(
                onSuccess = { mesas ->
                    if (mesas.isEmpty()) {
                        _mesasState.value = MesasState.Empty
                    } else {
                        _mesasState.value = MesasState.Success(mesas)
                    }
                },
                onFailure = { exception ->
                    _mesasState.value = MesasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    // ==================== HOLDS ====================

    /**
     * Crear Hold - NO reinicia el contador
     */
    fun crearHold(mesaId: Int, inicio: String, horas: Int, notas: String? = null) {
        _holdState.value = HoldState.Loading

        viewModelScope.launch {
            val request = CrearHoldRequest(
                mesa_id = mesaId,
                actor_tipo = tipoActor.valor,
                inicio = inicio,
                horas = horas,
                ttl_minutes = 3,
                notas = notas
            )

            val result = reservasRepository.crearHold(request)

            result.fold(
                onSuccess = { hold ->
                    _holdActivo.value = hold
                    _holdState.value = HoldState.Success(hold)
                    // NO iniciar contador aquí - ya está corriendo
                },
                onFailure = { exception ->
                    _holdState.value = HoldState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    /**
     * Cancelar Hold actual sin detener el contador
     */
    fun cancelarHoldActivo(motivo: String? = "Cambio de mesa") {
        val hold = _holdActivo.value ?: return

        viewModelScope.launch {
            reservasRepository.cancelarHold(hold.id_hold_mesa, motivo)
            _holdActivo.value = null
            // NO detener contador - sigue corriendo
        }
    }

    // ==================== RESERVAS ====================

    fun loadReservas(sucursalId: Int, estatus: Int? = null) {
        _reservasState.value = ReservasState.Loading

        viewModelScope.launch {
            val request = ListarReservasRequest(
                sucursal_id = sucursalId,
                estatus = estatus
            )

            val result = reservasRepository.listarReservas(request)

            result.fold(
                onSuccess = { reservas ->
                    if (reservas.isEmpty()) {
                        _reservasState.value = ReservasState.Empty
                    } else {
                        _reservasState.value = ReservasState.Success(reservas)
                    }
                },
                onFailure = { exception ->
                    _reservasState.value = ReservasState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearReserva(
        holdId: Int,
        inicio: String,
        finEstimado: String,
        toleranciaMin: Int = 15,
        notas: String? = null
    ) {
        _crearReservaState.value = CrearReservaState.Loading

        viewModelScope.launch {
            val request = when (tipoActor) {
                TipoActor.CLIENTE -> CrearReservaRequest(
                    cliente_id = usuarioId,
                    hold_id = holdId,
                    recepcionista_id = null,
                    inicio = inicio,
                    fin_estimado = finEstimado,
                    tolerancia_min = toleranciaMin,
                    notas = notas
                )
                TipoActor.RECEPCIONISTA -> CrearReservaRequest(
                    cliente_id = null,
                    hold_id = holdId,
                    recepcionista_id = usuarioId,
                    inicio = inicio,
                    fin_estimado = finEstimado,
                    tolerancia_min = toleranciaMin,
                    notas = notas
                )
            }

            val result = reservasRepository.crearReserva(request)

            result.fold(
                onSuccess = { reserva ->
                    // Reserva exitosa - detener todo
                    detenerContador()
                    _holdActivo.value = null
                    _crearReservaState.value = CrearReservaState.Success(reserva)
                },
                onFailure = { exception ->
                    _crearReservaState.value = CrearReservaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun iniciarReserva(reservaId: Int) {
        _accionReservaState.value = AccionReservaState.Loading

        viewModelScope.launch {
            val result = reservasRepository.iniciarReserva(reservaId)

            result.fold(
                onSuccess = { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Reserva iniciada", reserva)
                },
                onFailure = { exception ->
                    _accionReservaState.value = AccionReservaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun completarReserva(reservaId: Int) {
        _accionReservaState.value = AccionReservaState.Loading

        viewModelScope.launch {
            val result = reservasRepository.completarReserva(reservaId)

            result.fold(
                onSuccess = { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Reserva completada", reserva)
                },
                onFailure = { exception ->
                    _accionReservaState.value = AccionReservaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun cancelarReserva(reservaId: Int, motivo: String?) {
        _accionReservaState.value = AccionReservaState.Loading

        viewModelScope.launch {
            val result = reservasRepository.cancelarReserva(reservaId, motivo)

            result.fold(
                onSuccess = { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Reserva cancelada", reserva)
                },
                onFailure = { exception ->
                    _accionReservaState.value = AccionReservaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun marcarNoShow(reservaId: Int) {
        _accionReservaState.value = AccionReservaState.Loading

        viewModelScope.launch {
            val result = reservasRepository.marcarNoShow(reservaId)

            result.fold(
                onSuccess = { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Marcado como No Show", reserva)
                },
                onFailure = { exception ->
                    _accionReservaState.value = AccionReservaState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }

    fun resetCrearReservaState() {
        _crearReservaState.value = CrearReservaState.Idle
    }

    fun resetAccionReservaState() {
        _accionReservaState.value = AccionReservaState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        detenerContador()
    }
}

// States
sealed class MesasState {
    object Loading : MesasState()
    object Empty : MesasState()
    data class Success(val mesas: List<MesaDto>) : MesasState()
    data class Error(val message: String) : MesasState()
}

sealed class HoldState {
    object Idle : HoldState()
    object Loading : HoldState()
    object Expired : HoldState()
    data class Success(val hold: HoldDto) : HoldState()
    data class Error(val message: String) : HoldState()
}

sealed class ReservasState {
    object Loading : ReservasState()
    object Empty : ReservasState()
    data class Success(val reservas: List<ReservaDto>) : ReservasState()
    data class Error(val message: String) : ReservasState()
}

sealed class CrearReservaState {
    object Idle : CrearReservaState()
    object Loading : CrearReservaState()
    data class Success(val reserva: ReservaDto) : CrearReservaState()
    data class Error(val message: String) : CrearReservaState()
}

sealed class AccionReservaState {
    object Idle : AccionReservaState()
    object Loading : AccionReservaState()
    data class Success(val message: String, val reserva: ReservaDto) : AccionReservaState()
    data class Error(val message: String) : AccionReservaState()
}