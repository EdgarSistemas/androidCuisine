package com.intellisoft.androidcuisine.domain.repository.campania

import com.intellisoft.androidcuisine.data.remote.dto.*

interface CampaniaRepository {
    // Campañas
    suspend fun getCampanias(): Result<List<CampaniaDto>>
    suspend fun getCampania(id: Int): Result<CampaniaDto>
    suspend fun crearCampania(request: CrearCampaniaRequest): Result<CampaniaDto>
    suspend fun activarCampania(id: Int): Result<String>
    suspend fun desactivarCampania(id: Int): Result<String>

    // Métricas
    suspend fun getClientesVIP(topN: Int): Result<List<ClienteVIPDto>>
    suspend fun getClientesFrecuentes(topN: Int): Result<List<ClienteFrecuenteDto>>
    suspend fun getClientesInactivos(dias: Int): Result<List<ClienteInactivoDto>>
    suspend fun getClientesNuevos(dias: Int): Result<List<ClienteNuevoDto>>
    suspend fun getClientesPorCanal(): Result<List<ClienteCanalDto>>

    // Generar campaña
    suspend fun generarCampaniaDesdeMetrica(request: GenerarCampaniaMetricaRequest): Result<GenerarCampaniaMetricaResponse>
}