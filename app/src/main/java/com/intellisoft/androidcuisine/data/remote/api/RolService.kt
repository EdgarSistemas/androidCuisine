package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.ApiResponse
import com.intellisoft.androidcuisine.data.remote.dto.RolItemDto
import retrofit2.Response
import retrofit2.http.GET

interface RolService {
    @GET("roles")
    // Cambia RolDto -> RolItemDto
    suspend fun getRoles(): Response<ApiResponse<List<RolItemDto>>>
}