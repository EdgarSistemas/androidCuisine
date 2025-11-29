package com.intellisoft.androidcuisine.data.repository

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.*
import com.intellisoft.androidcuisine.domain.repository.ReservasRepository

class ReservasRepositoryImpl : ReservasRepository {

    override suspend fun getMesas(sucursalId: Int): Result<List<MesaDto>> {
        return try {
            val response = ApiClient.recepcionService.getMesas(sucursalId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener mesas"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearHold(request: CrearHoldRequest): Result<HoldDto> {
        return try {
            val response = ApiClient.recepcionService.crearHold(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.hold != null) {
                    Result.success(body.hold)
                } else {
                    Result.failure(Exception(body.message ?: "Error al crear hold"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarHold(holdId: Int, motivo: String?): Result<HoldDto> {
        return try {
            val request = CancelarHoldRequest(motivo = motivo)
            val response = ApiClient.recepcionService.cancelarHold(holdId, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.hold != null) {
                    Result.success(body.hold)
                } else {
                    Result.failure(Exception(body.message ?: "Error al cancelar hold"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verificarDisponibilidad(request: VerificarDisponibilidadRequest): Result<VerificarDisponibilidadResponse> {
        return try {
            val response = ApiClient.recepcionService.verificarDisponibilidad(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun crearReserva(request: CrearReservaRequest): Result<ReservaDto> {
        return try {
            val response = ApiClient.recepcionService.crearReserva(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.reserva != null) {
                    Result.success(body.reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Error al crear reserva"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun listarReservas(request: ListarReservasRequest): Result<List<ReservaDto>> {
        return try {
            val response = ApiClient.recepcionService.listarReservas(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(body.reservas ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReserva(reservaId: Int): Result<ReservaDto> {
        return try {
            val response = ApiClient.recepcionService.getReserva(reservaId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val reserva = body.reserva ?: body.data
                if (reserva != null) {
                    Result.success(reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Reserva no encontrada"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun iniciarReserva(reservaId: Int): Result<ReservaDto> {
        return try {
            val response = ApiClient.recepcionService.iniciarReserva(reservaId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.reserva != null) {
                    Result.success(body.reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Error al iniciar reserva"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completarReserva(reservaId: Int): Result<ReservaDto> {
        return try {
            val response = ApiClient.recepcionService.completarReserva(reservaId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.reserva != null) {
                    Result.success(body.reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Error al completar reserva"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarReserva(reservaId: Int, motivo: String?): Result<ReservaDto> {
        return try {
            val request = CancelarReservaRequest(motivo = motivo)
            val response = ApiClient.recepcionService.cancelarReserva(reservaId, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.reserva != null) {
                    Result.success(body.reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Error al cancelar reserva"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarNoShow(reservaId: Int): Result<ReservaDto> {
        return try {
            val response = ApiClient.recepcionService.marcarNoShow(reservaId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.reserva != null) {
                    Result.success(body.reserva)
                } else {
                    Result.failure(Exception(body.message ?: "Error al marcar no-show"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}