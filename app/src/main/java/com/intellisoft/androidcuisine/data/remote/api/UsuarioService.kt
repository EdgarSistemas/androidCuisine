package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.ApiResponse
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioCreateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateRequest
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioService {

    @PUT("usuarios/{usuario_id}")
    suspend fun updateUsuario(
        @Path("usuario_id") usuarioId: Int,
        @Body request: UsuarioUpdateRequest
    ): Response<UsuarioUpdateResponse>



    //implementacion  para CRUD
    // 1. Listar Usuarios con filtros (según tu requerimiento /filtrar)
    @GET("usuarios/filtrar")
    suspend fun getUsuarios(
        @Query("rol_id") rolId: Int?,
        @Query("sucursal_id") sucursalId: Int?
    ): Response<ApiResponse<List<UsuarioDto>>>

    // 2. Crear Nuevo Empleado
    @POST("usuarios/empleado")
    suspend fun createUsuario(
        @Body request: UsuarioCreateRequest
    ): Response<ApiResponse<UsuarioDto>>

    // 3. Actualizar Usuario ya se usa arriba

    // 4. Eliminar Usuario
    @DELETE("usuarios/{usuario_id}")
    suspend fun deleteUsuario(
        @Path("usuario_id") usuarioId: Int
    ): Response<ApiResponse<Any>>
}