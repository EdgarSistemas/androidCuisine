package com.intellisoft.androidcuisine.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val plataforma: String = "android",
    val push_token: String
)

data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val es_admin: Boolean,
    val es_cliente: Boolean,
    val acepta_marketing: Boolean,
    val mostrar_empresas: Boolean,
    val tipo_acceso: String,
    val modulos: List<ModuloDto>,
    val roles: List<RolDto>,
    val sucursales: List<Sucursal>
)

data class ModuloDto(
    val id_modulo: Int,
    val clave: String,
    val nombre: String,
    val descripcion: String
)

data class RolDto(
    val id_rol: Int,
    val nombre: String,
    val descripcion: String
)