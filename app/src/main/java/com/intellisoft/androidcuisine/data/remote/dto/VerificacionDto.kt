package com.intellisoft.androidcuisine.data.remote.dto

data class SolicitarRequest(
    val email: String
)

data class VerificarCodigoRequest(
    val codigo: String,
    val email: String
)
data class VerificacionYSolicitudResponse(
    val message: String,
    val success: Boolean
)

data class restablecerRequest(
    val codigo: String,
    val email: String,
    val nueva_password: String
)

