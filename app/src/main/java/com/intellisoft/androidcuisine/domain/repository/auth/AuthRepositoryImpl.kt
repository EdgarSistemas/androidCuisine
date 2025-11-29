package com.intellisoft.androidcuisine.domain.repository.auth

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.CambiarPasswordRequest
import com.intellisoft.androidcuisine.data.remote.dto.CambiarPasswordResponse
import com.intellisoft.androidcuisine.data.remote.dto.LoginRequest
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(
        email: String,
        password: String,
        fcmToken: String
    ): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password,
                push_token = fcmToken
            )

            val response = ApiClient.authService.login(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Modificacion para cambiar contraseña del usuario
    override suspend fun cambiarPassword(actual: String, nueva: String, confirmacion: String): Result<CambiarPasswordResponse> {
        return try {
            val request = CambiarPasswordRequest(actual, nueva, confirmacion)
            val response = ApiClient.authService.cambiarPassword(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    // Si success es false, usamos el mensaje o el error que viene del API
                    Result.failure(Exception(body.message ?: body.error ?: "Error al cambiar contraseña"))
                }
            } else {
                // Manejo de error 400 u otros códigos HTTP
                val errorBody = response.errorBody()?.string()
                // Intentamos extraer el mensaje del JSON de error si es posible, o usamos uno genérico
                Result.failure(Exception("Error del servidor: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}