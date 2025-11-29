package com.intellisoft.androidcuisine.domain.repository.mejoras

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.MejoraItemDto
import com.intellisoft.androidcuisine.data.remote.dto.MejoraRequest

class MejorasRepositoryImpl : MejorasRepository {

    override suspend fun enviarMejora(notas: String): Result<MejoraItemDto> {
        return try {
            val response = ApiClient.mejorasService.enviarMejora(MejoraRequest(notas))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    // Devolvemos el objeto creado
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al crear la sugerencia"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error del servidor: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMejoras(estatus: Int?): Result<List<MejoraItemDto>> {
        return try {
            val response = ApiClient.mejorasService.getMejoras(estatus)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    // Devolvemos la lista (o lista vacía si es null)
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception("Error al obtener la lista"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}