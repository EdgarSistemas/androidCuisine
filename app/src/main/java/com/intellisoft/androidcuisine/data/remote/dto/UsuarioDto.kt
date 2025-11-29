package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request
data class UsuarioUpdateRequest(
    val nombre: String,
    val apellido: String,
    val email: String
)

// Response (Lo que recibes)
data class UsuarioUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioUpdateData?
)

// Data simplificada: Solo mapeamos lo que nos sirve para confirmar o depurar
data class UsuarioUpdateData(
    @SerializedName("id_usuario")
    val idUsuario: Int,
    val nombre: String,
    val apellido: String,
    val email: String


)