package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.SolicitarRequest
import com.intellisoft.androidcuisine.data.remote.dto.VerificacionYSolicitudResponse
import com.intellisoft.androidcuisine.data.remote.dto.VerificarCodigoRequest
import com.intellisoft.androidcuisine.data.remote.dto.restablecerRequest
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
        @Body request: VerificarCodigoRequest // 🟢 CORRECCIÓN: Usar una sola Data Class para el body
    ): Response<VerificacionYSolicitudResponse>

    @POST ("/api/auth/password-reset/restablecer")
    suspend fun restablecerSolicitud(
        @Body request: restablecerRequest // 🟢 CORRECCIÓN: Usar la data class para el body
    ): Response<VerificacionYSolicitudResponse>

    }

