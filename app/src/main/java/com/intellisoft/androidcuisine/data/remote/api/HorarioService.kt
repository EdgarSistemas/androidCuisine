package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.HorarioUsuarioResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HorarioService {
    @GET("horarios/usuario/{userId}")
    suspend fun getHorarioUsuario(@Path("userId") userId: Int): Response<HorarioUsuarioResponse>
}