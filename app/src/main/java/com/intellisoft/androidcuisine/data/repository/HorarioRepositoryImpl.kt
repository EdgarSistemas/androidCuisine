package com.intellisoft.androidcuisine.data.repository

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.HorarioUsuarioData
import com.intellisoft.androidcuisine.domain.repository.HorarioRepository

class HorarioRepositoryImpl : HorarioRepository {

    override suspend fun getHorarioUsuario(userId: Int): Result<HorarioUsuarioData> {
        return try {
            val response = ApiClient.horarioService.getHorarioUsuario(userId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message ?: "Error al obtener horario"))
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