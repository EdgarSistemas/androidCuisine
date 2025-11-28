package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// === CAMBIO CLAVE: Renombramos a RolItemDto para que no choque con AuthDto ===
data class RolItemDto(
    @SerializedName("id_rol") val id: Int, // ✅ CORRECTO: Mapeamos "id_rol" a la variable "id"
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String?
) : Serializable

// ==============================================================================

data class UsuarioDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String?,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("roles") val roles: List<RolItemDto>? // <--- Aquí usamos la nueva clase
) : Serializable

data class CreateUsuarioDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String,

    // CORRECCIÓN: El POST espera "rol_id" y "sucursal_id"
    // (A diferencia del GET que devolvía "id_rol")
    @SerializedName("rol_id") val rolId: Int,
    @SerializedName("sucursal_id") val sucursalId: Int
)
data class UpdateUsuarioDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido") val apellido: String,
    @SerializedName("email") val email: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)