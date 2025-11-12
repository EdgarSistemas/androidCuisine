package com.intellisoft.androidcuisine.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val plataforma: String = "android",
    val push_token: String
)
