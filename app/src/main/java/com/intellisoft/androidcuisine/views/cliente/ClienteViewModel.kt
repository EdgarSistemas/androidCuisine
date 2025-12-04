package com.intellisoft.androidcuisine.views.cliente


import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.cliente.ClienteRepository
import com.intellisoft.androidcuisine.domain.repository.cliente.ClienteRepositoryImpl
import kotlinx.coroutines.launch

class ClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClienteRepository = ClienteRepositoryImpl()

    // SharedPreferences para persistir datos
    private val prefs = application.getSharedPreferences("cliente_prefs", Context.MODE_PRIVATE)

    // ==================== DATOS DEL CLIENTE ====================
    private var _clienteId: Int = 0
    val clienteId: Int get() = _clienteId

    fun setClienteId(id: Int) {
        _clienteId = id
    }

    // ==================== SELECCIÓN DE SUCURSAL/ÁREA/MESA ====================

    // Sucursal seleccionada
    private val _sucursalSeleccionada = MutableLiveData<SucursalActivaDto?>()
    val sucursalSeleccionada: LiveData<SucursalActivaDto?> = _sucursalSeleccionada

    // Área seleccionada
    private val _areaSeleccionada = MutableLiveData<AreaClienteDto?>()
    val areaSeleccionada: LiveData<AreaClienteDto?> = _areaSeleccionada

    // Mesa seleccionada
    private val _mesaSeleccionada = MutableLiveData<MesaClienteDto?>()
    val mesaSeleccionada: LiveData<MesaClienteDto?> = _mesaSeleccionada

    // ==================== ESTADOS DE CARGA ====================

    // Sucursales
    private val _sucursalesState = MutableLiveData<SucursalesState>(SucursalesState.Idle)
    val sucursalesState: LiveData<SucursalesState> = _sucursalesState

    // Áreas
    private val _areasState = MutableLiveData<AreasState>(AreasState.Idle)
    val areasState: LiveData<AreasState> = _areasState

    // Mesas
    private val _mesasState = MutableLiveData<MesasState>(MesasState.Idle)
    val mesasState: LiveData<MesasState> = _mesasState

    // Disponibilidad
    private val _disponibilidadState = MutableLiveData<DisponibilidadState>(DisponibilidadState.Idle)
    val disponibilidadState: LiveData<DisponibilidadState> = _disponibilidadState

    // Hold
    private val _holdState = MutableLiveData<HoldState>(HoldState.Idle)
    val holdState: LiveData<HoldState> = _holdState

    // Hold activo
    private val _holdActivo = MutableLiveData<HoldDto?>()
    val holdActivo: LiveData<HoldDto?> = _holdActivo

    // Reserva
    private val _crearReservaState = MutableLiveData<CrearReservaState>(CrearReservaState.Idle)
    val crearReservaState: LiveData<CrearReservaState> = _crearReservaState

    // Mis Reservas
    private val _misReservasState = MutableLiveData<MisReservasState>(MisReservasState.Idle)
    val misReservasState: LiveData<MisReservasState> = _misReservasState

    // Cancelar Reserva
    private val _cancelarReservaState = MutableLiveData<CancelarReservaState>(CancelarReservaState.Idle)
    val cancelarReservaState: LiveData<CancelarReservaState> = _cancelarReservaState

    // ==================== MENÚ ====================

    private val _categoriasState = MutableLiveData<CategoriasState>(CategoriasState.Idle)
    val categoriasState: LiveData<CategoriasState> = _categoriasState

    private val _productosState = MutableLiveData<ProductosState>(ProductosState.Idle)
    val productosState: LiveData<ProductosState> = _productosState

    private val _combosState = MutableLiveData<CombosState>(CombosState.Idle)
    val combosState: LiveData<CombosState> = _combosState

    // ==================== PEDIDOS ====================

    private val _crearPedidoState = MutableLiveData<CrearPedidoState>(CrearPedidoState.Idle)
    val crearPedidoState: LiveData<CrearPedidoState> = _crearPedidoState

    private val _misPedidosState = MutableLiveData<MisPedidosState>(MisPedidosState.Idle)
    val misPedidosState: LiveData<MisPedidosState> = _misPedidosState

    private val _pedidoDetalleState = MutableLiveData<PedidoDetalleState>(PedidoDetalleState.Idle)
    val pedidoDetalleState: LiveData<PedidoDetalleState> = _pedidoDetalleState

    private val _agregarItemState = MutableLiveData<AgregarItemState>(AgregarItemState.Idle)
    val agregarItemState: LiveData<AgregarItemState> = _agregarItemState

    // ==================== CUPONES ====================

    private val _misCuponesState = MutableLiveData<MisCuponesState>(MisCuponesState.Idle)
    val misCuponesState: LiveData<MisCuponesState> = _misCuponesState

    private val _validarCuponState = MutableLiveData<ValidarCuponState>(ValidarCuponState.Idle)
    val validarCuponState: LiveData<ValidarCuponState> = _validarCuponState

    // ==================== CALIFICACIONES ====================

    private val _crearCalificacionState = MutableLiveData<CrearCalificacionState>(CrearCalificacionState.Idle)
    val crearCalificacionState: LiveData<CrearCalificacionState> = _crearCalificacionState

    // ==================== MEJORAS ====================

    private val _crearMejoraState = MutableLiveData<CrearMejoraState>(CrearMejoraState.Idle)
    val crearMejoraState: LiveData<CrearMejoraState> = _crearMejoraState

    private val _accionReservaState = MutableLiveData<AccionReservaState>(AccionReservaState.Idle)
    val accionReservaState: LiveData<AccionReservaState> = _accionReservaState

    // ==================== CONTADOR HOLD (3 minutos) ====================

    private var holdCountdownTimer: CountDownTimer? = null
    private val _holdTiempoRestante = MutableLiveData<Long>(0)
    val holdTiempoRestante: LiveData<Long> = _holdTiempoRestante

    private val _holdExpirado = MutableLiveData<Boolean>(false)
    val holdExpirado: LiveData<Boolean> = _holdExpirado

    // ==================== INIT - RECUPERAR DATOS GUARDADOS ====================

    init {
        recuperarSucursalGuardada()
    }

    // ==================== FUNCIONES - PERSISTENCIA SUCURSAL ====================

    private fun recuperarSucursalGuardada() {
        val sucursalId = prefs.getInt("sucursal_id", 0)
        val sucursalNombre = prefs.getString("sucursal_nombre", null)
        val sucursalCodigo = prefs.getString("sucursal_codigo", null)
        val sucursalTelefono = prefs.getString("sucursal_telefono", null)
        val sucursalDireccion = prefs.getString("sucursal_direccion", null)

        if (sucursalId > 0 && sucursalNombre != null) {
            _sucursalSeleccionada.value = SucursalActivaDto(
                id_sucursal = sucursalId,
                codigo_sucursal = sucursalCodigo ?: "",
                nombre = sucursalNombre,
                telefono = sucursalTelefono,
                direccion = sucursalDireccion,
                es_activa = true
            )
        }
    }

    private fun guardarSucursal(sucursal: SucursalActivaDto) {
        prefs.edit()
            .putInt("sucursal_id", sucursal.id_sucursal)
            .putString("sucursal_nombre", sucursal.nombre)
            .putString("sucursal_codigo", sucursal.codigo_sucursal)
            .putString("sucursal_telefono", sucursal.telefono)
            .putString("sucursal_direccion", sucursal.direccion)
            .apply()
    }

    private fun limpiarSucursalGuardada() {
        prefs.edit()
            .remove("sucursal_id")
            .remove("sucursal_nombre")
            .remove("sucursal_codigo")
            .remove("sucursal_telefono")
            .remove("sucursal_direccion")
            .apply()
    }

    // ==================== FUNCIONES - CATÁLOGOS ====================

    fun cargarSucursales() {
        _sucursalesState.value = SucursalesState.Loading
        viewModelScope.launch {
            repository.getSucursalesActivas()
                .onSuccess { sucursales ->
                    _sucursalesState.value = if (sucursales.isEmpty()) {
                        SucursalesState.Empty
                    } else {
                        SucursalesState.Success(sucursales)
                    }
                }
                .onFailure { error ->
                    _sucursalesState.value = SucursalesState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun seleccionarSucursal(sucursal: SucursalActivaDto) {
        _sucursalSeleccionada.value = sucursal
        _areaSeleccionada.value = null
        _mesaSeleccionada.value = null
        guardarSucursal(sucursal)  // Persistir en SharedPreferences
        cargarAreas(sucursal.id_sucursal)
    }

    fun cargarAreas(sucursalId: Int) {
        _areasState.value = AreasState.Loading
        viewModelScope.launch {
            repository.getAreas(sucursalId)
                .onSuccess { areas ->
                    _areasState.value = if (areas.isEmpty()) {
                        AreasState.Empty
                    } else {
                        AreasState.Success(areas)
                    }
                }
                .onFailure { error ->
                    _areasState.value = AreasState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun seleccionarArea(area: AreaClienteDto) {
        _areaSeleccionada.value = area
        _mesaSeleccionada.value = null
        cargarMesas(_sucursalSeleccionada.value?.id_sucursal ?: 0, area.id_area)
    }

    fun cargarMesas(sucursalId: Int, areaId: Int? = null) {
        _mesasState.value = MesasState.Loading
        viewModelScope.launch {
            repository.getMesas(sucursalId, areaId)
                .onSuccess { mesas ->
                    _mesasState.value = if (mesas.isEmpty()) {
                        MesasState.Empty
                    } else {
                        MesasState.Success(mesas)
                    }
                }
                .onFailure { error ->
                    _mesasState.value = MesasState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun seleccionarMesa(mesa: MesaClienteDto) {
        _mesaSeleccionada.value = mesa
    }

    // ==================== FUNCIONES - HOLDS Y RESERVAS ====================

    fun verificarDisponibilidad(mesaId: Int, inicio: String, finEstimado: String) {
        _disponibilidadState.value = DisponibilidadState.Loading
        viewModelScope.launch {
            repository.verificarDisponibilidad(mesaId, inicio, finEstimado)
                .onSuccess { response ->
                    _disponibilidadState.value = DisponibilidadState.Success(response)
                }
                .onFailure { error ->
                    _disponibilidadState.value = DisponibilidadState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun crearHold(
        mesaId: Int,
        inicio: String,
        horas: Int,
        ttlMinutes: Int = 3,
        notas: String? = null
    ) {
        _holdState.value = HoldState.Loading
        viewModelScope.launch {
            repository.crearHold(mesaId, inicio, horas, ttlMinutes, notas)
                .onSuccess { hold ->
                    _holdActivo.value = hold
                    _holdState.value = HoldState.Success(hold)
                    iniciarContadorHold(ttlMinutes * 60 * 1000L)
                }
                .onFailure { error ->
                    _holdState.value = HoldState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun confirmarHold(holdId: Int) {
        _holdState.value = HoldState.Loading
        viewModelScope.launch {
            repository.confirmarHold(holdId)
                .onSuccess { response ->
                    response.hold?.let { hold ->
                        _holdActivo.value = hold
                        _holdState.value = HoldState.Confirmed(hold)
                    }
                }
                .onFailure { error ->
                    _holdState.value = HoldState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cancelarHold(holdId: Int, motivo: String? = null) {
        viewModelScope.launch {
            repository.cancelarHold(holdId, motivo)
                .onSuccess {
                    detenerContadorHold()
                    _holdActivo.value = null
                    _holdState.value = HoldState.Idle
                }
                .onFailure { error ->
                    _holdState.value = HoldState.Error(error.message ?: "Error desconocido")
                }
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
            repository.crearReserva(
                holdId = holdId,
                clienteId = _clienteId,
                inicio = inicio,
                finEstimado = finEstimado,
                toleranciaMin = toleranciaMin,
                notas = notas
            )
                .onSuccess { reserva ->
                    detenerContadorHold()
                    _holdActivo.value = null
                    _crearReservaState.value = CrearReservaState.Success(reserva)
                }
                .onFailure { error ->
                    _crearReservaState.value = CrearReservaState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cargarMisReservas(
        clienteId: Int? = null,
        estatus: Int? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null
    ) {
        _misReservasState.value = MisReservasState.Loading
        viewModelScope.launch {
            repository.listarReservas(clienteId ?: _clienteId, null, estatus, fechaDesde, fechaHasta)
                .onSuccess { reservas ->
                    val listaReservas = reservas ?: emptyList()
                    _misReservasState.value = if (listaReservas.isEmpty()) {
                        MisReservasState.Empty
                    } else {
                        MisReservasState.Success(listaReservas)
                    }
                }
                .onFailure { error ->
                    _misReservasState.value = MisReservasState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cancelarReserva(reservaId: Int, motivo: String? = null) {
        _cancelarReservaState.value = CancelarReservaState.Loading
        viewModelScope.launch {
            repository.cancelarReserva(reservaId, motivo)
                .onSuccess { reserva ->
                    _cancelarReservaState.value = CancelarReservaState.Success(reserva)
                }
                .onFailure { error ->
                    _cancelarReservaState.value = CancelarReservaState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun iniciarReserva(reservaId: Int) {
        _accionReservaState.value = AccionReservaState.Loading
        viewModelScope.launch {
            repository.iniciarReserva(reservaId)
                .onSuccess { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Reserva iniciada", reserva)
                    cargarMisReservas()
                }
                .onFailure { error ->
                    _accionReservaState.value = AccionReservaState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun completarReserva(reservaId: Int) {
        _accionReservaState.value = AccionReservaState.Loading
        viewModelScope.launch {
            repository.completarReserva(reservaId)
                .onSuccess { reserva ->
                    _accionReservaState.value = AccionReservaState.Success("Reserva completada", reserva)
                    cargarMisReservas()
                }
                .onFailure { error ->
                    _accionReservaState.value = AccionReservaState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun resetAccionReservaState() {
        _accionReservaState.value = AccionReservaState.Idle
    }

    fun resetCancelarReservaState() {
        _cancelarReservaState.value = CancelarReservaState.Idle
    }

    // ==================== FUNCIONES - MENÚ ====================

    fun cargarCategorias() {
        _categoriasState.value = CategoriasState.Loading
        viewModelScope.launch {
            repository.getCategorias()
                .onSuccess { categorias ->
                    _categoriasState.value = if (categorias.isEmpty()) {
                        CategoriasState.Empty
                    } else {
                        CategoriasState.Success(categorias)
                    }
                }
                .onFailure { error ->
                    _categoriasState.value = CategoriasState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cargarProductos() {
        _productosState.value = ProductosState.Loading
        viewModelScope.launch {
            repository.getProductos()
                .onSuccess { productos ->
                    _productosState.value = if (productos.isEmpty()) {
                        ProductosState.Empty
                    } else {
                        ProductosState.Success(productos)
                    }
                }
                .onFailure { error ->
                    _productosState.value = ProductosState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cargarCombos() {
        _combosState.value = CombosState.Loading
        viewModelScope.launch {
            repository.getCombos()
                .onSuccess { combos ->
                    _combosState.value = if (combos.isEmpty()) {
                        CombosState.Empty
                    } else {
                        CombosState.Success(combos)
                    }
                }
                .onFailure { error ->
                    _combosState.value = CombosState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    // ==================== FUNCIONES - PEDIDOS ====================

    fun crearPedidoDineIn(
        reservaId: Int,
        sucursalId: Int? = null,
        notas: String? = null,
        items: List<ItemPedidoRequest>? = null
    ) {
        val finalSucursalId = sucursalId ?: _sucursalSeleccionada.value?.id_sucursal

        if (finalSucursalId == null || finalSucursalId == 0) {
            _crearPedidoState.value = CrearPedidoState.Error("No se pudo determinar la sucursal")
            return
        }

        if (_clienteId == 0) {
            _crearPedidoState.value = CrearPedidoState.Error("Cliente no identificado")
            return
        }

        _crearPedidoState.value = CrearPedidoState.Loading
        viewModelScope.launch {
            repository.crearPedidoDineIn(finalSucursalId, _clienteId, reservaId, notas, items)
                .onSuccess { pedido ->
                    _crearPedidoState.value = CrearPedidoState.Success(pedido)
                }
                .onFailure { error ->
                    _crearPedidoState.value = CrearPedidoState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun crearPedidoTakeaway(
        sucursalId: Int,
        notas: String? = null,
        items: List<ItemPedidoRequest>? = null
    ) {
        _crearPedidoState.value = CrearPedidoState.Loading
        viewModelScope.launch {
            repository.crearPedidoTakeaway(sucursalId, _clienteId, notas, items)
                .onSuccess { pedido ->
                    _crearPedidoState.value = CrearPedidoState.Success(pedido)
                }
                .onFailure { error ->
                    _crearPedidoState.value = CrearPedidoState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun agregarItemPedido(
        pedidoId: Int,
        productoId: Int? = null,
        comboId: Int? = null,
        cantidad: Int,
        notas: String? = null
    ) {
        _agregarItemState.value = AgregarItemState.Loading
        viewModelScope.launch {
            repository.agregarItem(pedidoId, productoId, comboId, cantidad, notas)
                .onSuccess { item ->
                    _agregarItemState.value = AgregarItemState.Success(item)
                }
                .onFailure { error ->
                    _agregarItemState.value = AgregarItemState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cargarMisPedidos(
        estado: Int? = null,
        tipoPedido: Int? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null
    ) {
        _misPedidosState.value = MisPedidosState.Loading
        viewModelScope.launch {
            repository.listarPedidos(_clienteId, null, estado, tipoPedido, fechaDesde, fechaHasta)
                .onSuccess { pedidos ->
                    val listaPedidos = pedidos ?: emptyList()
                    _misPedidosState.value = if (listaPedidos.isEmpty()) {
                        MisPedidosState.Empty
                    } else {
                        MisPedidosState.Success(listaPedidos)
                    }
                }
                .onFailure { error ->
                    _misPedidosState.value = MisPedidosState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cargarDetallePedido(pedidoId: Int) {
        _pedidoDetalleState.value = PedidoDetalleState.Loading
        viewModelScope.launch {
            repository.getPedido(pedidoId)
                .onSuccess { pedido ->
                    _pedidoDetalleState.value = PedidoDetalleState.Success(pedido)
                }
                .onFailure { error ->
                    _pedidoDetalleState.value = PedidoDetalleState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun completarPedido(pedidoId: Int) {
        viewModelScope.launch {
            repository.completarPedido(pedidoId)
                .onSuccess { pedido ->
                    _pedidoDetalleState.value = PedidoDetalleState.Success(pedido)
                }
                .onFailure { error ->
                    _pedidoDetalleState.value = PedidoDetalleState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun cancelarPedido(pedidoId: Int, comentario: String? = null) {
        viewModelScope.launch {
            repository.cancelarPedido(pedidoId, comentario)
                .onSuccess { pedido ->
                    _pedidoDetalleState.value = PedidoDetalleState.Success(pedido)
                }
                .onFailure { error ->
                    _pedidoDetalleState.value = PedidoDetalleState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    // ==================== FUNCIONES - CUPONES ====================

    fun cargarMisCupones(vigentes: Boolean = true) {
        _misCuponesState.value = MisCuponesState.Loading
        viewModelScope.launch {
            repository.getMisCupones(vigentes)
                .onSuccess { cupones ->
                    _misCuponesState.value = if (cupones.isEmpty()) {
                        MisCuponesState.Empty
                    } else {
                        MisCuponesState.Success(cupones)
                    }
                }
                .onFailure { error ->
                    _misCuponesState.value = MisCuponesState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun validarCupon(codigo: String) {
        _validarCuponState.value = ValidarCuponState.Loading
        viewModelScope.launch {
            repository.validarCupon(codigo, _clienteId)
                .onSuccess { response ->
                    _validarCuponState.value = ValidarCuponState.Success(response)
                }
                .onFailure { error ->
                    _validarCuponState.value = ValidarCuponState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    // ==================== FUNCIONES - CALIFICACIONES ====================

    fun crearCalificacion(
        pedidoId: Int,
        calificacion: Int,
        empleadoId: Int? = null,
        notas: String? = null
    ) {
        _crearCalificacionState.value = CrearCalificacionState.Loading
        viewModelScope.launch {
            repository.crearCalificacion(pedidoId, calificacion, empleadoId, notas)
                .onSuccess { calif ->
                    _crearCalificacionState.value = CrearCalificacionState.Success(calif)
                }
                .onFailure { error ->
                    _crearCalificacionState.value = CrearCalificacionState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    // ==================== FUNCIONES - MEJORAS ====================

    fun crearMejora(notas: String) {
        _crearMejoraState.value = CrearMejoraState.Loading
        viewModelScope.launch {
            repository.crearMejora(notas)
                .onSuccess { mejora ->
                    _crearMejoraState.value = CrearMejoraState.Success(mejora)
                }
                .onFailure { error ->
                    _crearMejoraState.value = CrearMejoraState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    // ==================== CONTADOR HOLD ====================

    private fun iniciarContadorHold(millisInFuture: Long) {
        detenerContadorHold()
        _holdExpirado.value = false

        holdCountdownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _holdTiempoRestante.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _holdTiempoRestante.value = 0
                _holdExpirado.value = true
                _holdActivo.value = null
                _holdState.value = HoldState.Expired
            }
        }.start()
    }

    private fun detenerContadorHold() {
        holdCountdownTimer?.cancel()
        holdCountdownTimer = null
    }

    // ==================== UTILIDADES ====================

    fun limpiarSeleccion() {
        _sucursalSeleccionada.value = null
        _areaSeleccionada.value = null
        _mesaSeleccionada.value = null
        _holdActivo.value = null
        limpiarSucursalGuardada()
        detenerContadorHold()
    }

    fun resetCrearReservaState() {
        _crearReservaState.value = CrearReservaState.Idle
    }

    fun resetCrearPedidoState() {
        _crearPedidoState.value = CrearPedidoState.Idle
    }

    fun resetAgregarItemState() {
        _agregarItemState.value = AgregarItemState.Idle
    }

    fun resetValidarCuponState() {
        _validarCuponState.value = ValidarCuponState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        detenerContadorHold()
    }
}

// ==================== SEALED CLASSES - ESTADOS ====================

// Sucursales
sealed class SucursalesState {
    object Idle : SucursalesState()
    object Loading : SucursalesState()
    object Empty : SucursalesState()
    data class Success(val sucursales: List<SucursalActivaDto>) : SucursalesState()
    data class Error(val message: String) : SucursalesState()
}

// Áreas
sealed class AreasState {
    object Idle : AreasState()
    object Loading : AreasState()
    object Empty : AreasState()
    data class Success(val areas: List<AreaClienteDto>) : AreasState()
    data class Error(val message: String) : AreasState()
}

// Mesas
sealed class MesasState {
    object Idle : MesasState()
    object Loading : MesasState()
    object Empty : MesasState()
    data class Success(val mesas: List<MesaClienteDto>) : MesasState()
    data class Error(val message: String) : MesasState()
}

// Disponibilidad
sealed class DisponibilidadState {
    object Idle : DisponibilidadState()
    object Loading : DisponibilidadState()
    data class Success(val response: VerificarDisponibilidadResponse) : DisponibilidadState()
    data class Error(val message: String) : DisponibilidadState()
}

// Hold
sealed class HoldState {
    object Idle : HoldState()
    object Loading : HoldState()
    object Expired : HoldState()
    data class Success(val hold: HoldDto) : HoldState()
    data class Confirmed(val hold: HoldDto) : HoldState()
    data class Error(val message: String) : HoldState()
}

// Crear Reserva
sealed class CrearReservaState {
    object Idle : CrearReservaState()
    object Loading : CrearReservaState()
    data class Success(val reserva: ReservaClienteDto) : CrearReservaState()
    data class Error(val message: String) : CrearReservaState()
}

// Mis Reservas
sealed class MisReservasState {
    object Idle : MisReservasState()
    object Loading : MisReservasState()
    object Empty : MisReservasState()
    data class Success(val reservas: List<ReservaClienteDto>) : MisReservasState()
    data class Error(val message: String) : MisReservasState()
}

// Cancelar Reserva
sealed class CancelarReservaState {
    object Idle : CancelarReservaState()
    object Loading : CancelarReservaState()
    data class Success(val reserva: ReservaClienteDto) : CancelarReservaState()
    data class Error(val message: String) : CancelarReservaState()
}

// Categorías
sealed class CategoriasState {
    object Idle : CategoriasState()
    object Loading : CategoriasState()
    object Empty : CategoriasState()
    data class Success(val categorias: List<CategoriaDto>) : CategoriasState()
    data class Error(val message: String) : CategoriasState()
}

// Productos
sealed class ProductosState {
    object Idle : ProductosState()
    object Loading : ProductosState()
    object Empty : ProductosState()
    data class Success(val productos: List<ProductoDto>) : ProductosState()
    data class Error(val message: String) : ProductosState()
}

// Combos
sealed class CombosState {
    object Idle : CombosState()
    object Loading : CombosState()
    object Empty : CombosState()
    data class Success(val combos: List<ComboDto>) : CombosState()
    data class Error(val message: String) : CombosState()
}

// Crear Pedido
sealed class CrearPedidoState {
    object Idle : CrearPedidoState()
    object Loading : CrearPedidoState()
    data class Success(val pedido: PedidoClienteDto) : CrearPedidoState()
    data class Error(val message: String) : CrearPedidoState()
}

// Mis Pedidos
sealed class MisPedidosState {
    object Idle : MisPedidosState()
    object Loading : MisPedidosState()
    object Empty : MisPedidosState()
    data class Success(val pedidos: List<PedidoClienteDto>) : MisPedidosState()
    data class Error(val message: String) : MisPedidosState()
}

// Pedido Detalle
sealed class PedidoDetalleState {
    object Idle : PedidoDetalleState()
    object Loading : PedidoDetalleState()
    data class Success(val pedido: PedidoClienteDto) : PedidoDetalleState()
    data class Error(val message: String) : PedidoDetalleState()
}

// Agregar Item
sealed class AgregarItemState {
    object Idle : AgregarItemState()
    object Loading : AgregarItemState()
    data class Success(val item: ItemPedidoDto) : AgregarItemState()
    data class Error(val message: String) : AgregarItemState()
}

// Mis Cupones
sealed class MisCuponesState {
    object Idle : MisCuponesState()
    object Loading : MisCuponesState()
    object Empty : MisCuponesState()
    data class Success(val cupones: List<CuponClienteDto>) : MisCuponesState()
    data class Error(val message: String) : MisCuponesState()
}

// Validar Cupón
sealed class ValidarCuponState {
    object Idle : ValidarCuponState()
    object Loading : ValidarCuponState()
    data class Success(val response: ValidarCuponResponse) : ValidarCuponState()
    data class Error(val message: String) : ValidarCuponState()
}

// Crear Calificación
sealed class CrearCalificacionState {
    object Idle : CrearCalificacionState()
    object Loading : CrearCalificacionState()
    data class Success(val calificacion: CalificacionDto) : CrearCalificacionState()
    data class Error(val message: String) : CrearCalificacionState()
}

// Crear Mejora
sealed class CrearMejoraState {
    object Idle : CrearMejoraState()
    object Loading : CrearMejoraState()
    data class Success(val mejora: MejoraDto) : CrearMejoraState()
    data class Error(val message: String) : CrearMejoraState()
}

sealed class AccionReservaState {
    object Idle : AccionReservaState()
    object Loading : AccionReservaState()
    data class Success(val message: String, val reserva: ReservaClienteDto) : AccionReservaState()
    data class Error(val message: String) : AccionReservaState()
}