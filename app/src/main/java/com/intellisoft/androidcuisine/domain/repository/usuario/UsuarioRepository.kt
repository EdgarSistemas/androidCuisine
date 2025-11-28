package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse
import com.intellisoft.androidcuisine.data.remote.dto.*

interface UsuarioRepository {
    suspend fun updateUsuario(usuarioId: Int, request: UsuarioUpdateRequest):
            Result<UsuarioUpdateResponse>

    suspend fun getUsuarios(rolId: Int?, sucursalId: Int?):
            Result<List<UsuarioDto>>

    suspend fun createUsuario(request: UsuarioCreateRequest):
            Result<UsuarioDto>

    suspend fun deleteUsuario(usuarioId: Int):
            Result<Boolean>

    suspend fun getRoles():
                Result<List<RolDto>>
}