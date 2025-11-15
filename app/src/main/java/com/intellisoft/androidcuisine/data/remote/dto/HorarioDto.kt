package com.intellisoft.androidcuisine.data.remote.dto

data class HorarioUsuarioResponse(
    val success: Boolean,
    val data: HorarioUsuarioData? = null,
    val message: String? = null
)

data class HorarioUsuarioData(
    val horario: HorarioInfo,
    val detalles: List<HorarioDetalle>,
    val usuario_horario: UsuarioHorarioInfo
)

data class HorarioInfo(
    val id_horario: Int,
    val nombre: String,
    val descripcion: String,
    val clave: String,
    val es_activo: Boolean,
    val sucursal_id: Int,
    val created_at: String
)

data class HorarioDetalle(
    val id_detalle: Int,
    val horario_id: Int,
    val dia_semana: Int,
    val hora_inicio: String,
    val hora_fin: String,
    val turno_idx: Int,
    val tolerancia_min: Int,
    val es_activo: Boolean
)

data class UsuarioHorarioInfo(
    val id_usuario_horario: Int,
    val usuario_id: Int,
    val fecha_inicio: String,
    val fecha_fin: String,
    val es_recurring: Boolean
)