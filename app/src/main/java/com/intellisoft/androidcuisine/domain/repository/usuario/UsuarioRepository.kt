package com.intellisoft.androidcuisine.domain.repository.usuario

import com.intellisoft.androidcuisine.data.remote.dto.CreateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.RolDto
import com.intellisoft.androidcuisine.data.remote.dto.RolItemDto
import com.intellisoft.androidcuisine.data.remote.dto.UpdateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto

interface UsuarioRepository {
    suspend fun getUsuarios(rolId: Int?, sucursalId: Int?): Result<List<UsuarioDto>>
    suspend fun createUsuario(request: CreateUsuarioDto): Result<UsuarioDto> // Usar CreateUsuarioDto
    suspend fun updateUsuario(usuarioId: Int, request: UpdateUsuarioDto): Result<UsuarioDto> // Usar UpdateUsuarioDto
    suspend fun deleteUsuario(usuarioId: Int): Result<Boolean>
    suspend fun getRoles(): Result<List<RolItemDto>>
}