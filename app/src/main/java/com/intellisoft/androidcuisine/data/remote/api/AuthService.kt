package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.CambiarPasswordRequest
import com.intellisoft.androidcuisine.data.remote.dto.CambiarPasswordResponse
import com.intellisoft.androidcuisine.data.remote.dto.LoginRequest
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    //Modificacion para cambiar contraseña del usuario
    @POST("auth/cambiar-password")
    suspend fun cambiarPassword(@Body request: CambiarPasswordRequest): Response<CambiarPasswordResponse>
}