package com.intellisoft.androidcuisine.domain.repository.cliente

import com.intellisoft.androidcuisine.data.remote.dto.*

interface ClienteRepository {

    // ==================== CATÁLOGOS ====================
    suspend fun getSucursalesActivas(): Result<List<SucursalActivaDto>>
    suspend fun getSucursal(sucursalId: Int): Result<SucursalActivaDto>
    suspend fun getAreas(sucursalId: Int): Result<List<AreaClienteDto>>
    suspend fun getMesas(sucursalId: Int, areaId: Int? = null): Result<List<MesaClienteDto>>

    // ==================== MENÚ ====================
    suspend fun getCategorias(): Result<List<CategoriaDto>>
    suspend fun getProductos(): Result<List<ProductoDto>>
    suspend fun getProducto(productoId: Int): Result<ProductoDto>
    suspend fun getCombos(): Result<List<ComboDto>>
    suspend fun getCombo(comboId: Int): Result<ComboDto>

    // ==================== HOLDS ====================
    suspend fun verificarDisponibilidad(
        mesaId: Int,
        inicio: String,
        finEstimado: String
    ): Result<VerificarDisponibilidadResponse>

    suspend fun crearHold(
        mesaId: Int,
        inicio: String,
        horas: Int,
        ttlMinutes: Int = 3,
        notas: String? = null
    ): Result<HoldDto>

    suspend fun confirmarHold(holdId: Int): Result<ConfirmarHoldResponse>
    suspend fun cancelarHold(holdId: Int, motivo: String? = null): Result<HoldDto>

    // ==================== RESERVAS ====================
    suspend fun crearReserva(
        holdId: Int,
        clienteId: Int,  // <-- Agregar parámetro
        inicio: String,
        finEstimado: String,
        toleranciaMin: Int = 15,
        notas: String? = null
    ): Result<ReservaClienteDto>

    suspend fun getReserva(reservaId: Int): Result<ReservaClienteDto>

    suspend fun listarReservas(
        clienteId: Int? = null,
        sucursalId: Int? = null,
        estatus: Int? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null
    ): Result<List<ReservaClienteDto>>

    suspend fun cancelarReserva(reservaId: Int, motivo: String? = null): Result<ReservaClienteDto>

    suspend fun iniciarReserva(reservaId: Int): Result<ReservaClienteDto>
    suspend fun completarReserva(reservaId: Int): Result<ReservaClienteDto>

    // ==================== PEDIDOS ====================
    suspend fun crearPedidoDineIn(
        sucursalId: Int,
        clienteId: Int,
        reservaId: Int,
        notas: String? = null,
        items: List<ItemPedidoRequest>? = null
    ): Result<PedidoClienteDto>

    suspend fun crearPedidoTakeaway(
        sucursalId: Int,
        clienteId: Int,
        notas: String? = null,
        items: List<ItemPedidoRequest>? = null
    ): Result<PedidoClienteDto>

    suspend fun agregarItem(
        pedidoId: Int,
        productoId: Int? = null,
        comboId: Int? = null,
        cantidad: Int,
        notas: String? = null
    ): Result<ItemPedidoDto>

    suspend fun getPedido(pedidoId: Int): Result<PedidoClienteDto>

    suspend fun listarPedidos(
        clienteId: Int? = null,
        sucursalId: Int? = null,
        estado: Int? = null,
        tipoPedido: Int? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null
    ): Result<List<PedidoClienteDto>>

    suspend fun getPedidosActivos(sucursalId: Int): Result<List<PedidoClienteDto>>
    suspend fun completarPedido(pedidoId: Int): Result<PedidoClienteDto>
    suspend fun cancelarPedido(pedidoId: Int, comentario: String? = null): Result<PedidoClienteDto>

    // ==================== CUPONES ====================
    suspend fun getMisCupones(vigentes: Boolean = true): Result<List<CuponClienteDto>>
    suspend fun getCuponesCliente(clienteId: Int): Result<List<CuponClienteDto>>
    suspend fun validarCupon(codigo: String, clienteId: Int): Result<ValidarCuponResponse>
    suspend fun getCampanias(): Result<List<CampaniaDto>>

    // ==================== PAGOS ====================
    suspend fun crearPago(
        pedidoId: Int,
        sucursalId: Int,
        monto: Double,
        propina: Double = 0.0,
        campaniaUsuarioId: Int? = null,
        montoDescontado: Double? = null
    ): Result<PagoDto>

    // ==================== CALIFICACIONES ====================
    suspend fun crearCalificacion(
        pedidoId: Int,
        calificacion: Int,
        empleadoId: Int? = null,
        notas: String? = null
    ): Result<CalificacionDto>

    suspend fun getCalificaciones(
        pedidoId: Int? = null,
        empleadoId: Int? = null
    ): Result<List<CalificacionDto>>

    suspend fun getCalificacion(calificacionId: Int): Result<CalificacionDto>

    // ==================== MEJORAS ====================
    suspend fun crearMejora(notas: String): Result<MejoraDto>
    suspend fun getMejoras(estatus: Int? = null): Result<List<MejoraDto>>
    suspend fun getMejora(mejoraId: Int): Result<MejoraDto>
}