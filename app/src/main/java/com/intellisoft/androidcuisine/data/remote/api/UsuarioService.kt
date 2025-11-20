package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path


interface UsuarioService {

    @PUT("usuarios/{usuario_id}")
    suspend fun updateUsuario(
        @Path("usuario_id") usuarioId: Int,
        @Body request: UsuarioUpdateRequest
    ): Response<UsuarioUpdateResponse>

}