package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.ApiResponse
import com.intellisoft.androidcuisine.data.remote.dto.CreateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UpdateUsuarioDto
import com.intellisoft.androidcuisine.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface UsuarioService {

    // =================================================================================
    // LISTAR USUARIOS (GET)
    // =================================================================================
    // Antes: Response<List<UsuarioDto>>
    // Ahora: Response<ApiResponse<List<UsuarioDto>>>  <-- ESTO ARREGLA EL ERROR

    //    @GET("usuarios/filtrar")
//    suspend fun getUsuarios(
//        @Query("rol_id") rolId: Int,
//        @Query("sucursal_id") sucursalId: Int
//    ): Response<ApiResponse<List<UsuarioDto>>>
    @GET("usuarios")
    suspend fun getUsuarios(): Response<ApiResponse<List<UsuarioDto>>>
    // =================================================================================
    // CREAR USUARIO (POST)
    // =================================================================================
    @POST("usuarios/empleado")
    suspend fun createUsuario(
        @Body usuario: CreateUsuarioDto
    ): Response<ApiResponse<UsuarioDto>>

    // =================================================================================
    // ACTUALIZAR USUARIO (PUT)
    // =================================================================================
    @PUT("usuarios/{usuario_id}")
    suspend fun updateUsuario(
        @Path("usuario_id") usuarioId: Int,
        @Body usuario: UpdateUsuarioDto
    ): Response<ApiResponse<UsuarioDto>>

    // =================================================================================
    // ELIMINAR USUARIO (DELETE)
    // =================================================================================
    @DELETE("usuarios/{usuario_id}")
    suspend fun deleteUsuario(
        @Path("usuario_id") usuarioId: Int
    ): Response<ApiResponse<Any>> // Delete suele devolver success:true sin data compleja
}