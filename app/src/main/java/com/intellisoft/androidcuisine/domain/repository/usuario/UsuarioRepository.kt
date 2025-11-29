package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse

interface UsuarioRepository {
    suspend fun updateUsuario(usuarioId: Int, request: UsuarioUpdateRequest):
            Result<UsuarioUpdateResponse>
}