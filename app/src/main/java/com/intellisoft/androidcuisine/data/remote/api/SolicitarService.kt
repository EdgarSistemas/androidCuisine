package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.SolicitarRequest
import com.intellisoft.androidcuisine.data.remote.dto.VerificacionYSolicitudResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SolicitarService {
    @POST("/api/auth/email-validation/solicitar")
        suspend fun crearSolicitud(
        @Body solicitud: SolicitarRequest
    ): Response<VerificacionYSolicitudResponse>

    @POST("/api/auth/email-validation/verificar")
    suspend fun verficiarSolicitud(
       // @Body codigo: VerificarCodigoRequest forma alterna
        @Body codigo: String,
        @Body email: String
    ): Response<VerificacionYSolicitudResponse>

    @POST ("/api/auth/password-reset/restablecer")
    suspend fun restablecerSolicitud(
        @Body codigo: String,
        @Body email: String,
        @Body nueva_password: String
    ): Response<VerificacionYSolicitudResponse>

    }

