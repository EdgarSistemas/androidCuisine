package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.RolDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioCreateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
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

    override suspend fun getUsuarios(rolId: Int?, sucursalId: Int?): Result<List<UsuarioDto>> {
        return try {
            val response = ApiClient.usuarioService.getUsuarios(rolId, sucursalId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al listar usuarios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUsuario(request: UsuarioCreateRequest): Result<UsuarioDto> {
        return try {
            val response = ApiClient.usuarioService.createUsuario(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUsuario(usuarioId: Int): Result<Boolean> {
        return try {
            val response = ApiClient.usuarioService.deleteUsuario(usuarioId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al eliminar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoles(): Result<List<RolDto>> {
        return try {
            // Usamos el nuevo servicio de roles
            val response = ApiClient.rolService.getRoles()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener roles"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}