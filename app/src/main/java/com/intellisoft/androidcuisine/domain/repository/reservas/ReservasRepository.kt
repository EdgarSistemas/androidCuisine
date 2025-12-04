package com.intellisoft.androidcuisine.domain.repository.reservas

import com.intellisoft.androidcuisine.data.remote.dto.*

interface ReservasRepository {
    // Mesas
    suspend fun getMesas(sucursalId: Int): Result<List<MesaDto>>

    // Holds
    suspend fun crearHold(request: CrearHoldRequest): Result<HoldDto>
    suspend fun cancelarHold(holdId: Int, motivo: String?): Result<HoldDto>
    suspend fun verificarDisponibilidad(request: VerificarDisponibilidadRequest): Result<VerificarDisponibilidadResponse>

    // Reservas
    suspend fun crearReserva(request: CrearReservaRequest): Result<ReservaDto>
    suspend fun listarReservas(request: ListarReservasRequest): Result<List<ReservaDto>>
    suspend fun getReserva(reservaId: Int): Result<ReservaDto>
    suspend fun iniciarReserva(reservaId: Int): Result<ReservaDto>
    suspend fun completarReserva(reservaId: Int): Result<ReservaDto>
    suspend fun cancelarReserva(reservaId: Int, motivo: String?): Result<ReservaDto>
    suspend fun marcarNoShow(reservaId: Int): Result<ReservaDto>
}