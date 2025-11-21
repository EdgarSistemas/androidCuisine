package com.intellisoft.androidcuisine.data.remote.dto

import com.google.gson.annotations.SerializedName

// Request para enviar (POST)
data class MejoraRequest(
    val notas: String
)

// Objeto base de la mejora (común en GET y POST)
data class MejoraItemDto(
    @SerializedName("id_mejora")
    val idMejora: Int,

    @SerializedName("cliente_id")
    val clienteId: Int,

    val notas: String,

    val estatus: Int, // 1 = Registrada, etc.

    @SerializedName("created_at")
    val fechaCreacion: String
)

// Respuesta del POST (crear): data es un OBJETO
data class MejoraSingleResponse(
    val success: Boolean,
    val data: MejoraItemDto?, // Puede ser nulo si falla
    val message: String? = null // Opcional, por si el API manda error
)

// Respuesta del GET (listar): data es una LISTA
data class MejorasListResponse(
    val success: Boolean,
    val data: List<MejoraItemDto>? // Lista de objetos
)