package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse

class UsuarioRepositoryImpl : UsuarioRepository {

    override suspend fun updateUsuario(usuarioId: Int, request: UsuarioUpdateRequest): Result<UsuarioUpdateResponse> {
        return try {
            val response = ApiClient.usuarioService.updateUsuario(usuarioId, request)

            if (response.isSuccessful && response.body() != null) {
                if (response.body()!!.success) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.body()!!.message ?: "Error al actualizar"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error de servidor"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}