package com.intellisoft.androidcuisine.domain.repository

import com.intellisoft.androidcuisine.data.remote.dto.*

interface ClienteRepository {

    // Sucursales
    suspend fun getSucursalesActivas(): Result<List<SucursalActivaDto>>

    // Áreas
    suspend fun getAreas(sucursalId: Int): Result<List<AreaDto>>

    // Mesas
    suspend fun getMesas(sucursalId: Int, areaId: Int?): Result<List<MesaClienteDto>>

    // Holds
    suspend fun crearHold(mesaId: Int, inicio: String, fin: String): Result<HoldClienteDto>
    suspend fun cancelarHold(holdId: Int): Result<Unit>

    // Reservas
    suspend fun crearReserva(
        clienteId: Int,
        holdId: Int,
        inicio: String,
        finEstimado: String,
        toleranciaMin: Int,
        notas: String?
    ): Result<ReservaClienteDto>
    suspend fun listarReservas(clienteId: Int, estado: Int?): Result<List<ReservaClienteDto>>
    suspend fun cancelarReserva(reservaId: Int, motivo: String?): Result<String>

    // Pedidos
    suspend fun crearPedidoDineIn(request: CrearPedidoDineInRequest): Result<PedidoCreadoDto>
    suspend fun crearPedidoTakeaway(request: CrearPedidoTakeawayRequest): Result<PedidoCreadoDto>
    suspend fun getPedido(pedidoId: Int): Result<PedidoDetalleDto>
    suspend fun agregarItem(pedidoId: Int, request: AgregarItemRequest): Result<ItemAgregadoDto>
    suspend fun completarPedido(pedidoId: Int): Result<String>
    suspend fun cancelarPedido(pedidoId: Int, comentario: String?): Result<String>
    suspend fun listarPedidos(clienteId: Int, sucursalId: Int?, estado: Int?, tipoPedido: Int?): Result<List<PedidoDetalleDto>>

    // Cupones
    suspend fun getMisCupones(soloDisponibles: Boolean): Result<List<CuponClienteDto>>
    suspend fun validarCupon(codigo: String, clienteId: Int): Result<ValidarCuponResponse>

    // Calificaciones
    suspend fun crearCalificacion(request: CrearCalificacionRequest): Result<CalificacionDto>

    suspend fun getCategorias(): Result<List<CategoriaDto>>
    suspend fun getProductos(): Result<List<ProductoDto>>
    suspend fun getCombos(): Result<List<ComboDto>>
}