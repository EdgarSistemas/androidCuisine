package com.intellisoft.androidcuisine.data.repository

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.InsumoExistenciaDto
import com.intellisoft.androidcuisine.data.remote.dto.InsumosExistenciasRequest
import com.intellisoft.androidcuisine.domain.repository.InsumoRepository

class InsumoRepositoryImpl : InsumoRepository {

    override suspend fun getInsumosExistencias(sucursalId: Int): Result<List<InsumoExistenciaDto>> {
        return try {
            val request = InsumosExistenciasRequest(
                sucursal_id = sucursalId,
                solo_activos = true
            )

            val response = ApiClient.insumoService.getInsumosExistencias(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener insumos"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}