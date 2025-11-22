package com.intellisoft.androidcuisine.domain.repository.tickets

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.TicketDto
import com.intellisoft.androidcuisine.data.remote.dto.TicketRequest

interface TicketRepository {
    suspend fun getTickets(estatus: Int? = null): Result<List<TicketDto>>
    suspend fun crearTicket(notas: String, base64Image: String): Result<TicketDto>
    suspend fun cambiarEstatus(idTicket: Int, nuevoEstatus: Int): Result<Boolean>
}
