package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.TicketListResponse
import com.intellisoft.androidcuisine.data.remote.dto.TicketRequest
import com.intellisoft.androidcuisine.data.remote.dto.TicketSingleResponse
import retrofit2.Response
import retrofit2.http.*

interface TicketService {
    // Listar tickets filtrando opcionalmente por estatus
    @GET("tickets")
    suspend fun getTickets(
        @Query("estatus") estatus: Int? = null
    ): Response<TicketListResponse>

    // Crear ticket
    @POST("tickets")
    suspend fun createTicket(
        @Body ticket: TicketRequest
    ): Response<TicketSingleResponse>

    // Actualizar estatus (Para Admin/Gerente)
    @PUT("tickets/{id}") // Asumiendo ruta estándar de update
    suspend fun updateTicketEstatus(
        @Path("id") id: Int,
        @Body body: Map<String, Int> // Ej: {"estatus": 2}
    ): Response<TicketSingleResponse>
}