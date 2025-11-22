package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName

// Modelo para enviar al servidor (POST)
data class TicketRequest(
    val notas: String,
    @SerializedName("imagen_url") // El backend espera este nombre para el contenido Base64
    val imagenBase64: String
)

// Modelo de recepción (GET)
data class TicketDto(
    @SerializedName("id_ticket")
    val idTicket: Int,
    @SerializedName("usuarios_id") // Según tu descripción
    val usuarioId: Int,
    val notas: String,
    @SerializedName("imagen_url")
    val imagenUrl: String?, // Puede venir como URL http... o string Base64
    val estatus: Int, // 1=Registrado, 2=En Proceso, 3=Completado, 4=Cancelado
    @SerializedName("created_at")
    val fechaCreacion: String
)

data class TicketListResponse(
    val success: Boolean,
    val data: List<TicketDto>?,
    val message: String?
)

data class TicketSingleResponse(
    val success: Boolean,
    val data: TicketDto?,
    val message: String?
)