package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse

class UsuarioRepositoryImpl : UsuarioRepository {

    override suspend fun updateUsuario(usuarioId: Int, request: UsuarioUpdateRequest): Result<UsuarioUpdateResponse> {
        return try {
            val response = ApiClient.usuarioService.updateUsuario(usuarioId, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // Verificamos success del JSON
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.message ?: "Error al actualizar (API success false)"))
                }
            } else {
                // Manejo de errores HTTP (404, 500, etc)
                val errorMsg = response.errorBody()?.string() ?: "Error de servidor: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}