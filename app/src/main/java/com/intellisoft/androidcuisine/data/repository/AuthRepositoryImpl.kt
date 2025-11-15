package com.intellisoft.androidcuisine.data.repository

import com.intellisoft.androidcuisine.data.remote.api.ApiClient
import com.intellisoft.androidcuisine.data.remote.dto.LoginRequest
import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse
import com.intellisoft.androidcuisine.domain.repository.AuthRepository

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
}