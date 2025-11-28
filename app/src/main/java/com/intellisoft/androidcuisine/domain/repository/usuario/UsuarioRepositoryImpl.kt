package com.intellisoft.androidcuisine.domain.repository.usuario

import android.util.Log
import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.ApiResponse
import com.intellisoft.androidcuisine.data.remote.dto.CreateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.RolDto
import com.intellisoft.androidcuisine.data.remote.dto.RolItemDto
import com.intellisoft.androidcuisine.data.remote.dto.UpdateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import kotlin.math.log

class UsuarioRepositoryImpl : UsuarioRepository {

    // =================================================================================
    // LISTAR USUARIOS (GET)
    // =================================================================================
    override suspend fun getUsuarios(rolId: Int?, sucursalId: Int?): Result<List<UsuarioDto>> {
        return try {
            val sucursal = sucursalId ?: 0
            val rol = rolId ?: 0 // <--- AGREGAMOS ESTO (Si es null, usamos 0)

            Log.d("UsuarioRepo", "📡 Pidiendo usuarios... Rol: $rol | Sucursal: $sucursal")            // Ahora pasamos 'rol' que ya es Int seguro
            val response = ApiClient.usuarioService.getUsuarios(rol, sucursal)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Error al listar usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================================
    // CREAR USUARIO (POST)
    // =================================================================================
    override suspend fun createUsuario(request: CreateUsuarioDto): Result<UsuarioDto> {
        return try {
            val response = ApiClient.usuarioService.createUsuario(request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                // CORRECCIÓN: Extraemos .data
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(Exception(apiResponse.message ?: "Error al crear usuario"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================================
    // ACTUALIZAR USUARIO (PUT)
    // =================================================================================
    override suspend fun updateUsuario(usuarioId: Int, request: UpdateUsuarioDto): Result<UsuarioDto> {
        return try {
            val response = ApiClient.usuarioService.updateUsuario(usuarioId, request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                // CORRECCIÓN: Extraemos .data
                if (apiResponse.success && apiResponse.data != null) {
                    // Aquí asumimos que el backend devuelve el objeto UsuarioDto actualizado en 'data'
                    // Si devuelve otra cosa (ej. UsuarioUpdateData), tendrías que mapearlo.
                    // Por ahora usamos un cast seguro o asumimos que coincide.
                    try {
                        Result.success(apiResponse.data)
                    } catch (e: ClassCastException) {
                        Result.failure(Exception("Error de tipo de datos al actualizar"))
                    }
                } else {
                    Result.failure(Exception(apiResponse.message))
                }
            } else {
                Result.failure(Exception("Error al actualizar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================================
    // ELIMINAR USUARIO (DELETE)
    // =================================================================================
    override suspend fun deleteUsuario(usuarioId: Int): Result<Boolean> {
        return try {
            val response = ApiClient.usuarioService.deleteUsuario(usuarioId)

            // Delete suele devolver un ApiResponse sin data relevante, solo success=true
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("No se pudo eliminar el usuario"))
                }
            } else {
                Result.failure(Exception("Error al eliminar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================================
    // OBTENER ROLES (GET)
    // =================================================================================
    override suspend fun getRoles(): Result<List<RolItemDto>> {
        return try {
            val response = ApiClient.rolService.getRoles()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Error roles"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}