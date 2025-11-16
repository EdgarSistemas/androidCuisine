package com.intellisoft.androidcuisine.domain.repository.auth

import com.intellisoft.androidcuisine.data.remote.dto.LoginResponse

interface AuthRepository {
    suspend fun login(email: String, password: String, fcmToken: String): Result<LoginResponse>
}