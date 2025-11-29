package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReservasService {

    // Mesas
    @GET("mesas")
    suspend fun getMesas(
        @Query("sucursal_id") sucursalId: Int,
        @Query("solo_activas") soloActivas: Boolean = true
    ): Response<MesasResponse>

    // Holds
    @POST("holds")
    suspend fun crearHold(@Body request: CrearHoldRequest): Response<CrearHoldResponse>

    @POST("holds/{hold_id}/cancelar")
    suspend fun cancelarHold(
        @Path("hold_id") holdId: Int,
        @Body request: CancelarHoldRequest
    ): Response<CancelarHoldResponse>

    @POST("holds/disponibilidad")
    suspend fun verificarDisponibilidad(@Body request: VerificarDisponibilidadRequest): Response<VerificarDisponibilidadResponse>

    // Reservas
    @POST("reservas")
    suspend fun crearReserva(@Body request: CrearReservaRequest): Response<CrearReservaResponse>

    @POST("reservas/listar")
    suspend fun listarReservas(@Body request: ListarReservasRequest): Response<ListarReservasResponse>

    @GET("reservas/{reserva_id}")
    suspend fun getReserva(@Path("reserva_id") reservaId: Int): Response<ReservaDetalleResponse>

    @POST("reservas/{reserva_id}/iniciar")
    suspend fun iniciarReserva(@Path("reserva_id") reservaId: Int): Response<AccionReservaResponse>

    @POST("reservas/{reserva_id}/completar")
    suspend fun completarReserva(@Path("reserva_id") reservaId: Int): Response<AccionReservaResponse>

    @POST("reservas/{reserva_id}/cancelar")
    suspend fun cancelarReserva(
        @Path("reserva_id") reservaId: Int,
        @Body request: CancelarReservaRequest
    ): Response<AccionReservaResponse>

    @POST("reservas/{reserva_id}/no-show")
    suspend fun marcarNoShow(@Path("reserva_id") reservaId: Int): Response<AccionReservaResponse>
}