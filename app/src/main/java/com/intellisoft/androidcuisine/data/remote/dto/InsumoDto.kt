package com.intellisoft.androidcuisine.data.remote.dto


data class InsumosExistenciasRequest(
    val sucursal_id: Int,
    val solo_activos: Boolean = true
)

data class InsumosExistenciasResponse(
    val success: Boolean,
    val data: List<InsumoExistenciaDto>? = null,
    val message: String? = null
)

data class InsumoExistenciaDto(
    val id_insumo: Int,
    val nombre: String,
    val unidad_id: Int,
    val unidad_clave: String,
    val unidad_nombre: String,
    val es_activo: Boolean,
    val cantidad: Double,
    val costo_promedio: Double,
    val updated_at: String
)