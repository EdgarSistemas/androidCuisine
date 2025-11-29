package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ClienteService {

    // ==================== SUCURSALES ====================

    @GET("sucursales/activas")
    suspend fun getSucursalesActivas(): Response<SucursalesActivasResponse>

    @GET("sucursales/{sucursal_id}")
    suspend fun getSucursal(
        @Path("sucursal_id") sucursalId: Int
    ): Response<SucursalDetalleResponse>

    // ==================== ÁREAS ====================

    @GET("areas")
    suspend fun getAreas(
        @Query("sucursal_id") sucursalId: Int,
        @Query("solo_activas") soloActivas: Boolean = true
    ): Response<AreasResponse>

    // ==================== MESAS ====================

    @GET("mesas")
    suspend fun getMesas(
        @Query("sucursal_id") sucursalId: Int,
        @Query("area_id") areaId: Int? = null,
        @Query("solo_activas") soloActivas: Boolean = true
    ): Response<MesasClienteResponse>

    // ==================== HOLDS ====================

    @POST("holds")
    suspend fun crearHold(
        @Body request: CrearHoldClienteRequest
    ): Response<CrearHoldClienteResponse>

    @DELETE("holds/{hold_id}")
    suspend fun cancelarHold(
        @Path("hold_id") holdId: Int
    ): Response<Unit>

    // ==================== RESERVAS ====================

    @POST("reservas")
    suspend fun crearReserva(
        @Body request: CrearReservaClienteRequest
    ): Response<CrearReservaClienteResponse>

    @GET("reservas/{reserva_id}")
    suspend fun getReserva(
        @Path("reserva_id") reservaId: Int
    ): Response<ReservaClienteDto>

    @POST("reservas/listar")
    suspend fun listarReservas(
        @Body request: ListarReservasClienteRequest
    ): Response<ListarReservasClienteResponse>

    @POST("reservas/{reserva_id}/cancelar")
    suspend fun cancelarReserva(
        @Path("reserva_id") reservaId: Int,
        @Body request: CancelarReservaClienteRequest?
    ): Response<CancelarReservaClienteResponse>

    // ==================== PEDIDOS ====================

    @POST("pedidos")
    suspend fun crearPedidoDineIn(
        @Body request: CrearPedidoDineInRequest
    ): Response<CrearPedidoResponse>

    @POST("pedidos/para-llevar")
    suspend fun crearPedidoTakeaway(
        @Body request: CrearPedidoTakeawayRequest
    ): Response<CrearPedidoResponse>

    @GET("pedidos/{pedido_id}")
    suspend fun getPedido(
        @Path("pedido_id") pedidoId: Int
    ): Response<PedidoDetalleResponse>

    @POST("pedidos/{pedido_id}/items")
    suspend fun agregarItem(
        @Path("pedido_id") pedidoId: Int,
        @Body request: AgregarItemRequest
    ): Response<AgregarItemResponse>

    @PATCH("pedidos/{pedido_id}/completar")
    suspend fun completarPedido(
        @Path("pedido_id") pedidoId: Int
    ): Response<AccionPedidoResponse>

    @PATCH("pedidos/{pedido_id}/cancelar")
    suspend fun cancelarPedido(
        @Path("pedido_id") pedidoId: Int,
        @Body request: AccionPedidoRequest?
    ): Response<AccionPedidoResponse>

    @POST("pedidos/listar")
    suspend fun listarPedidos(
        @Body request: ListarPedidosClienteRequest
    ): Response<ListarPedidosClienteResponse>

    // ==================== CUPONES ====================

    @GET("campanias/cupones/mis-cupones")
    suspend fun getMisCupones(
        @Query("solo_disponibles") soloDisponibles: Boolean = true
    ): Response<MisCuponesResponse>

    @POST("campanias/cupones/validar")
    suspend fun validarCupon(
        @Body request: ValidarCuponRequest
    ): Response<ValidarCuponResponse>

    // ==================== CALIFICACIONES ====================

    @POST("calificaciones")
    suspend fun crearCalificacion(
        @Body request: CrearCalificacionRequest
    ): Response<CalificacionResponse>

    @GET("calificaciones")
    suspend fun listarCalificaciones(
        @Query("pedido_id") pedidoId: Int? = null
    ): Response<ListarCalificacionesResponse>

    // ==================== MENÚ ====================

    @GET("categorias")
    suspend fun getCategorias(): Response<CategoriasResponse>

    @GET("productos")
    suspend fun getProductos(): Response<ProductosResponse>

    @GET("productos/{producto_id}")
    suspend fun getProducto(
        @Path("producto_id") productoId: Int
    ): Response<ProductoDto>

    @GET("combos")
    suspend fun getCombos(): Response<CombosResponse>

    @GET("combos/{combo_id}")
    suspend fun getCombo(
        @Path("combo_id") comboId: Int
    ): Response<ComboDto>
}