package com.intellisoft.androidcuisine.data.remote.dto

data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val user: User
)

data class User(
    val acepta_marketing: Boolean,
    val apellido: String,
    val email: String,
    val es_admin: Boolean,
    val es_cliente: Boolean,
    val id: Int,
    val modulos: List<Modulo>,
    val mostrar_empresas: Boolean,
    val nombre: String,
    val roles: List<Rol>,
    val sucursales: List<Sucursal>,
    val telefono: String,
    val tipo_acceso: String
)

data class Modulo(
    val clave: String,
    val descripcion: String,
    val id_modulo: Int,
    val nombre: String
)

data class Rol(
    val descripcion: String,
    val id_rol: Int,
    val nombre: String
)
