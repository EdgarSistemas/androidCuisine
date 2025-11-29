package com.intellisoft.androidcuisine.data.remote.dto

data class ProveedoresResponse(
    val success: Boolean,
    val data: List<ProveedorDto>? = null,
    val message: String? = null
)

data class ProveedorDto(
    val id_proveedor: Int,
    val nombre: String,
    val telefono: String?,
    val email: String?,
    val es_activo: Boolean
)