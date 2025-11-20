package com.intellisoft.androidcuisine.data.remote.dto


data class UsuarioUpdateRequest(
    val nombre: String,
    val apellido: String,
    val email: String
)


data class UsuarioUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: UserDto
)