package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName

// ============================================================================================
//  REQUESTS (Objetos que envías a la API)
// ============================================================================================

/**
 * Petición para CREAR un nuevo usuario empleado.
 * Endpoint: POST /api/usuarios/empleado
 */
data class UsuarioCreateRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String,
    @SerializedName("rol_id") val rolId: Int,
    @SerializedName("sucursal_id") val sucursalId: Int
)

/**
 * Petición para ACTUALIZAR un usuario existente.
 * Endpoint: PUT /api/usuarios/{id}
 */
data class UsuarioUpdateRequest(
    val nombre: String,
    val apellido: String,
    val email: String
)

// ============================================================================================
//  RESPONSES (Objetos que recibes de la API)
// ============================================================================================

/**
 * Respuesta genérica o "envoltorio" que usa tu API para la mayoría de consultas.
 * Ejemplo de uso: ApiResponse<List<UsuarioDto>>
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

/**
 * Respuesta específica que devuelve la API tras actualizar un usuario.
 */
data class UsuarioUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioUpdateData?
)

// ============================================================================================
//  MODELS (Datos puros / Entidades)
// ============================================================================================

/**
 * Modelo principal de Usuario para mostrar en listas (RecyclerView).
 */
data class UsuarioDto(
    @SerializedName("id_usuario") val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    @SerializedName("rol_id") val rolId: Int?,     // ID del rol asociado
    val roles: List<RolDto>?                         // Lista de roles (si la API devuelve el objeto completo)
)

/**
 * Modelo simplificado que devuelve la API dentro de UsuarioUpdateResponse.
 */
data class UsuarioUpdateData(
    @SerializedName("id_usuario") val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val email: String
)

/**
 * Modelo de Rol para llenar el Spinner (Dropdown).
 */
data class RolDto(
    @SerializedName("id_rol") val idRol: Int,
    val nombre: String,
    val descripcion: String?
)