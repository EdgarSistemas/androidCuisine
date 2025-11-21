package com.intellisoft.androidcuisine.domain.repository.mejoras

import com.intellisoft.androidcuisine.data.remote.dto.MejoraItemDto

interface MejorasRepository {
    suspend fun enviarMejora(notas: String): Result<MejoraItemDto> // Cambio aquí
    suspend fun getMejoras(estatus: Int? = null): Result<List<MejoraItemDto>>
}