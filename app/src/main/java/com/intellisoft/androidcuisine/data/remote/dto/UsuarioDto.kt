package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request (Lo que envías) - Se queda igual
data class UsuarioUpdateRequest(
    val nombre: String,
    val apellido: String,
    val email: String
)

// Response (Lo que recibes)
data class UsuarioUpdateResponse(
    val success: Boolean,
    val message: String,
    // Puedes mapear data si quieres validar algo, o dejarlo incluso como Any? si no lo usas.
    // Pero lo correcto es usar una clase simplificada:
    val data: UsuarioUpdateData?
)

// Data simplificada: Solo mapeamos lo que nos sirve para confirmar o depurar
data class UsuarioUpdateData(
    @SerializedName("id_usuario")
    val idUsuario: Int,

    val nombre: String,
    val apellido: String,
    val email: String

    // NOTA: Hemos eliminado roles, sucursales, fechas, telefono, etc.
    // Gson ignorará esos campos automáticamente.
)