package com.intellisoft.androidcuisine.domain.repository.insumo

import com.intellisoft.androidcuisine.data.remote.dto.InsumoExistenciaDto

interface InsumoRepository {
    suspend fun getInsumosExistencias(sucursalId: Int): Result<List<InsumoExistenciaDto>>
}