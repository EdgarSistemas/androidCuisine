package com.intellisoft.androidcuisine.data.remote.dto

// Mesas
data class MesasResponse(
    val success: Boolean,
    val data: List<MesaDto>? = null,
    val total: Int? = null,
    val message: String? = null
)

data class MesaDto(
    val id_mesa: Int,
    val area_id: Int,
    val codigo_mesa: String,
    val capacidad: Int,
    val es_activa: Boolean,
    val estatus_actual: Int,
    val estatus_display: String
)

// Holds
data class CrearHoldRequest(
    val mesa_id: Int,
    val actor_tipo: Int, // 2 = Recepcionista
    val inicio: String,
    val horas: Int,
    val ttl_minutes: Int = 3,
    val notas: String? = null
)

data class CrearHoldResponse(
    val message: String? = null,
    val hold: HoldDto? = null,
    val success: Boolean? = null
)

data class HoldDto(
    val id_hold_mesa: Int,
    val mesa_id: Int,
    val estatus: Int,
    val actor_usuario_id: Int? = null,
    val inicio: String,
    val fin_estimado: String,
    val fechahora_expiracion: String
)

data class CancelarHoldRequest(
    val motivo: String? = null
)

data class CancelarHoldResponse(
    val message: String? = null,
    val hold: HoldDto? = null,
    val success: Boolean? = null
)

data class VerificarDisponibilidadRequest(
    val mesa_id: Int,
    val inicio: String,
    val fin_estimado: String
)

data class VerificarDisponibilidadResponse(
    val disponible: Boolean,
    val mensaje: String? = null,
    val success: Boolean? = null
)

// Reservas
data class CrearReservaRequest(
    val cliente_id: Int? = null, // null para recepcionista, ID para cliente
    val inicio: String,
    val fin_estimado: String,
    val hold_id: Int,
    val recepcionista_id: Int? = null, // null para cliente, ID para recepcionista
    val tolerancia_min: Int = 15,
    val notas: String? = null
)

data class CrearReservaResponse(
    val message: String? = null,
    val reserva: ReservaDto? = null,
    val success: Boolean? = null
)

data class ReservaDto(
    val id_reserva: Int,
    val cliente_id: Int? = null,
    val recepcionista_id: Int? = null,
    val inicio: String,
    val fin_estimado: String,
    val estatus: Int,
    val estatus_display: String,
    val tolerancia_min: Int? = null,
    val notas: String? = null,
    val hold_id: Int? = null,
    val puede_iniciar: Boolean = false,
    val mesa_id: Int? = null,
    val codigo_mesa: String? = null
)

data class ListarReservasRequest(
    val sucursal_id: Int? = null,
    val estatus: Int? = null,
    val fecha_desde: String? = null,
    val fecha_hasta: String? = null
)

data class ListarReservasResponse(
    val reservas: List<ReservaDto>? = null,
    val total: Int? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class ReservaDetalleResponse(
    val success: Boolean,
    val data: ReservaDto? = null,
    val reserva: ReservaDto? = null,
    val message: String? = null
)

data class CancelarReservaRequest(
    val motivo: String? = null
)

data class AccionReservaResponse(
    val message: String? = null,
    val reserva: ReservaDto? = null,
    val success: Boolean? = null
)