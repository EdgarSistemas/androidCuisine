package com.intellisoft.androidcuisine.data.remote.dto

data class SucursalDto(
    val codigo_sucursal: String,
    val created_at: String,
    val direccion: String,
    val es_activa: Boolean,
    val id_sucursal: Int,
    val nombre: String,
    val telefono: String,
    val updated_at: String
)

data class SucursalResponse(
    val data: List<SucursalDto>? = null,
    val success: Boolean,
    val message: String? = null
)

data class SucursalSingleResponse(
    val data: SucursalDto? = null,
    val success: Boolean,
    val message: String? = null
)

data class SucursalRequest(
    val direccion: String,
    val nombre: String,
    val telefono: String
)
