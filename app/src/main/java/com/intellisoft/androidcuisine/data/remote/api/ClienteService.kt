package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ClienteService {

    // ==================== CATÁLOGOS ====================

    @GET("sucursales/activas")
    suspend fun getSucursalesActivas(): Response<SucursalesActivasResponse>

    @GET("sucursales/{sucursal_id}")
    suspend fun getSucursal(
        @Path("sucursal_id") sucursalId: Int
    ): Response<SucursalActivaDto>

    @GET("areas")
    suspend fun getAreas(
        @Query("sucursal_id") sucursalId: Int,
        @Query("solo_activas") soloActivas: Boolean = true
    ): Response<AreasClienteResponse>

    @GET("mesas")
    suspend fun getMesas(
        @Query("sucursal_id") sucursalId: Int,
        @Query("area_id") areaId: Int? = null,
        @Query("solo_activas") soloActivas: Boolean = true
    ): Response<MesasClienteResponse>

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

    // ==================== HOLDS ====================

    @POST("holds/disponibilidad")
    suspend fun verificarDisponibilidad(
        @Body request: VerificarDisponibilidadRequest
    ): Response<VerificarDisponibilidadResponse>

    @POST("holds")
    suspend fun crearHold(
        @Body request: CrearHoldRequest
    ): Response<CrearHoldResponse>

    @POST("holds/{hold_id}/confirmar")
    suspend fun confirmarHold(
        @Path("hold_id") holdId: Int
    ): Response<ConfirmarHoldResponse>

    @POST("holds/{hold_id}/cancelar")
    suspend fun cancelarHold(
        @Path("hold_id") holdId: Int,
        @Body request: CancelarHoldRequest? = null
    ): Response<CancelarHoldResponse>

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
        @Body request: CancelarReservaRequest? = null
    ): Response<CancelarReservaResponse>

    // Iniciar reserva
    @POST("reservas/{reserva_id}/iniciar")
    suspend fun iniciarReserva(
        @Path("reserva_id") reservaId: Int
    ): Response<AccionReservaClienteResponse>

    // Completar reserva
    @POST("reservas/{reserva_id}/completar")
    suspend fun completarReserva(
        @Path("reserva_id") reservaId: Int
    ): Response<AccionReservaClienteResponse>

    // ==================== PEDIDOS ====================

    @POST("pedidos")
    suspend fun crearPedidoDineIn(
        @Body request: CrearPedidoDineInRequest
    ): Response<CrearPedidoResponse>

    @POST("pedidos/para-llevar")
    suspend fun crearPedidoTakeaway(
        @Body request: CrearPedidoTakeawayRequest
    ): Response<CrearPedidoResponse>

    @POST("pedidos/{pedido_id}/items")
    suspend fun agregarItem(
        @Path("pedido_id") pedidoId: Int,
        @Body request: ItemPedidoRequest
    ): Response<AgregarItemResponse>

    @GET("pedidos/{pedido_id}")
    suspend fun getPedido(
        @Path("pedido_id") pedidoId: Int
    ): Response<PedidoClienteDto>

    @POST("pedidos/listar")
    suspend fun listarPedidos(
        @Body request: ListarPedidosRequest
    ): Response<ListarPedidosResponse>

    @GET("pedidos/activos")
    suspend fun getPedidosActivos(
        @Query("sucursal_id") sucursalId: Int
    ): Response<ListarPedidosResponse>

    @PATCH("pedidos/{pedido_id}/completar")
    suspend fun completarPedido(
        @Path("pedido_id") pedidoId: Int
    ): Response<AccionPedidoResponse>

    @PATCH("pedidos/{pedido_id}/cancelar")
    suspend fun cancelarPedido(
        @Path("pedido_id") pedidoId: Int,
        @Body request: AccionPedidoRequest? = null
    ): Response<AccionPedidoResponse>

    // ==================== CUPONES ====================

    @GET("campanias/cupones/mis-cupones")
    suspend fun getMisCupones(
        @Query("vigentes") vigentes: Boolean = true
    ): Response<MisCuponesResponse>

    @GET("campanias/cupones/cliente/{cliente_id}")
    suspend fun getCuponesCliente(
        @Path("cliente_id") clienteId: Int
    ): Response<MisCuponesResponse>

    @POST("campanias/cupones/validar")
    suspend fun validarCupon(
        @Body request: ValidarCuponRequest
    ): Response<ValidarCuponResponse>

    @GET("campanias")
    suspend fun getCampanias(): Response<CampaniasResponse>

    // ==================== PAGOS ====================

    @POST("pagos")
    suspend fun crearPago(
        @Body request: CrearPagoRequest
    ): Response<CrearPagoResponse>

    // ==================== CALIFICACIONES ====================

    @POST("calificaciones")
    suspend fun crearCalificacion(
        @Body request: CrearCalificacionRequest
    ): Response<CalificacionResponse>

    @GET("calificaciones")
    suspend fun getCalificaciones(
        @Query("pedido_id") pedidoId: Int? = null,
        @Query("empleado_id") empleadoId: Int? = null
    ): Response<ListarCalificacionesResponse>

    @GET("calificaciones/{calificacion_id}")
    suspend fun getCalificacion(
        @Path("calificacion_id") calificacionId: Int
    ): Response<CalificacionDto>

    // ==================== MEJORAS ====================

    @POST("mejoras")
    suspend fun crearMejora(
        @Body request: CrearMejoraRequest
    ): Response<MejoraResponse>

    @GET("mejoras")
    suspend fun getMejoras(
        @Query("estatus") estatus: Int? = null
    ): Response<ListarMejorasResponse>

    @GET("mejoras/{mejora_id}")
    suspend fun getMejora(
        @Path("mejora_id") mejoraId: Int
    ): Response<MejoraDto>
}